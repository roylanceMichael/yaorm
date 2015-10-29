package org.roylance.yaorm.services

import com.google.gson.Gson
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.models.db.migration.MigrationModel
import org.roylance.yaorm.models.migration.*
import org.roylance.yaorm.utilities.CommonSqlDataTypeUtilities
import java.util.*

public abstract class EntityContext(
    protected val entityServices:List<IEntityService<*,*>>,
    protected val migrationService:IEntityService<Long, MigrationModel>,
    protected val contextName:String,
    private val gson:Gson = Gson()) {

    public fun getLatestMigrationDefinition() : DefinitionModels? {
        val whereItem = WhereClauseItem(
                MigrationModel.ContextName,
                WhereClauseItem.Equals,
                this.contextName)

        val migrations = this.migrationService.where(whereItem)

        if (!migrations.isEmpty()) {
            val lastMigration =  migrations
                    .sortedByDescending { it.id }
                    .first()

            return this.gson
                    .fromJson(
                            lastMigration.modelDefinitionJson,
                            DefinitionModels::class.java)
        }

        return null
    }

    public fun createNewMigration(id:Long) {
        val definitionsModels = this.getDefinitions()

        val migrationModel = MigrationModel(
                id,
                this.contextName,
                this.gson.toJson(definitionsModels))

        this.migrationService.create(migrationModel)
    }

    public fun getDifferenceReport() : DifferenceReportModel {
        val returnModels = ArrayList<DifferenceModel>()
        val latestMigration = this.getLatestMigrationDefinition() ?:
                return DifferenceReportModel(false, returnModels)

        val currentModel = this.getDefinitions()

        // TODO: implement in util classes
//        currentModel
//            .definitionModels
//            .forEach { currentDefinition ->
//                val foundDefinition = latestMigration.definitionModels
//                    .filter { currentDefinition.name.equals(it.name) }
//                    .firstOrNull()
//
//                if (foundDefinition == null) {
//                    val differenceModel = DifferenceModel(
//                            DifferenceModel.EntityTypeTable,
//                            DifferenceModel.OperationCreate,
//                            currentDefinition.name,
//                            definitionModel = currentDefinition)
//
//                    returnModels.add(differenceModel)
//                }
//                else {
//
//                }
//
//            }

        return DifferenceReportModel(true, returnModels)
    }

    public fun getDefinitions() : DefinitionModels {
        val returnList = ArrayList<DefinitionModel>()

        this.entityServices.forEach {
            val propertyNames = it.entityDefinition
                    .methods
                    .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Set) }
                    .map { it.name.substring(CommonSqlDataTypeUtilities.GetSetLength) }
                    .toHashSet()

            val propertyDefinitions = it.entityDefinition
                    .methods
                    .filter { it.name.startsWith(CommonSqlDataTypeUtilities.Get) &&
                            propertyNames.contains(it.name.substring(CommonSqlDataTypeUtilities.GetSetLength)) &&
                            !CommonSqlDataTypeUtilities.JavaObjectName.equals(it.genericReturnType.typeName) }
                    .map {
                        val name = CommonSqlDataTypeUtilities.lowercaseFirstChar(
                                it.name.substring(CommonSqlDataTypeUtilities.GetSetLength))
                        val javaType = it.returnType.name
                        PropertyDefinitionModel(name, javaType)
                    }

            returnList.add(DefinitionModel(
                    it.entityDefinition.simpleName,
                    propertyDefinitions,
                    it.indexDefinition))
        }

        return DefinitionModels(returnList)
    }
}

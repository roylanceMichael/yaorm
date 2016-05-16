package org.roylance.yaorm.services.map

import com.google.gson.Gson
import org.roylance.yaorm.models.WhereClauseItem
import org.roylance.yaorm.models.db.migration.MigrationModel
import org.roylance.yaorm.models.migration.DefinitionModel
import org.roylance.yaorm.models.migration.DefinitionModels
import org.roylance.yaorm.models.migration.DifferenceModel
import org.roylance.yaorm.models.migration.DifferenceReportModel
import org.roylance.yaorm.services.entity.IEntityService
import org.roylance.yaorm.utilities.DefinitionModelComparisonUtil
import java.util.*

class EntityMapContext(
        private  val entityService:IEntityMapService,
        private val definitions:DefinitionModels,
        private val migrationService: IEntityService<MigrationModel>,
        private val contextName:String,
        private val gson: Gson = Gson()) {
    private val typeToAction = object: HashMap<
            String,
            (definition: DefinitionModel, difference: DifferenceModel) -> Boolean>() {
        init {
            put(DifferenceModel.OperationCreate + DifferenceModel.EntityTypeColumn,
                    { definition, difference ->  createColumn(definition, difference) })
            put(DifferenceModel.OperationCreate + DifferenceModel.EntityTypeIndex,
                    { definition, difference ->  createIndex(definition, difference) })
            put(DifferenceModel.OperationCreate + DifferenceModel.EntityTypeTable,
                    { definition, difference ->  createTable(definition, difference) })
            put(DifferenceModel.OperationDrop + DifferenceModel.EntityTypeColumn,
                    { definition, difference ->  dropColumn(definition, difference) })
            put(DifferenceModel.OperationDrop + DifferenceModel.EntityTypeIndex,
                    { definition, difference ->  dropIndex(definition, difference) })
            put(DifferenceModel.OperationDrop + DifferenceModel.EntityTypeTable,
                    { definition, difference ->  dropTable(definition, difference) })
        }
    }

    init {
        this.migrationService.createTable()
    }

    fun handleMigrations(newId:String=UUID.randomUUID().toString()) {
        val differenceReport = this.getDifferenceReport()

        if (!differenceReport.migrationExists || differenceReport.differenceExists()) {
            this.applyMigrations(differenceReport)
            this.createNewMigration(newId)
        }
    }

    fun applyMigrations(differenceReportModel: DifferenceReportModel) {
        if (!differenceReportModel.migrationExists) {
            this.definitions
                .definitionModels
                .forEach {
                    this.entityService.createTable(it)
                }

            return
        }

        differenceReportModel
                .differences
                .forEach { differenceModel ->
                    val foundDefinition = this.definitions
                            .definitionModels
                            .filter { it.name.equals(differenceModel.name) }
                            .firstOrNull() ?: return@forEach

                    val key = "${differenceModel.operation}${differenceModel.entityType}"

                    if (this.typeToAction.containsKey(key)) {
                        this.typeToAction[key]!!.invoke(foundDefinition, differenceModel)
                    }
                }
    }

    fun getLatestMigrationDefinition() : DefinitionModels? {
        val whereItem = WhereClauseItem(
                MigrationModel.ContextName,
                WhereClauseItem.Equals,
                this.contextName)

        val migrations = this.migrationService.where(whereItem)

        if (!migrations.isEmpty()) {
            val lastMigration =  migrations
                    .sortedByDescending { it.insertDate }
                    .first()

            return this.gson
                    .fromJson(
                            lastMigration.modelDefinitionJson,
                            DefinitionModels::class.java)
        }

        return null
    }

    fun createNewMigration(id:String) {
        val migrationModel = MigrationModel(
                id,
                this.contextName,
                this.gson.toJson(this.definitions))

        this.migrationService.create(migrationModel)
    }

    fun getDifferenceReport() : DifferenceReportModel {
        val returnModels = ArrayList<DifferenceModel>()

        val latestMigration = this.getLatestMigrationDefinition()

        this.definitions
                .definitionModels
                .forEach { currentDefinition ->
                    if (latestMigration == null) {
                        DefinitionModelComparisonUtil
                                .addDifferenceIfDifferent(
                                        currentDefinition,
                                        null,
                                        returnModels)
                    }
                    else {
                        val foundDefinition = latestMigration.definitionModels
                                .filter { currentDefinition.name.equals(it.name) }
                                .firstOrNull()

                        // will look at children as well
                        DefinitionModelComparisonUtil
                                .addDifferenceIfDifferent(
                                        currentDefinition,
                                        foundDefinition,
                                        returnModels)
                    }
                }

        return DifferenceReportModel(
                latestMigration != null,
                returnModels)
    }

    private fun createIndex(definition: DefinitionModel, difference: DifferenceModel):Boolean {
        if (difference.indexModel != null) {
            return entityService.createIndex(difference.indexModel, definition)
        }
        return false
    }

    private fun dropIndex(definition: DefinitionModel, difference: DifferenceModel):Boolean {
        if (difference.indexModel != null) {
            return entityService.dropIndex(difference.indexModel, definition)
        }
        return false
    }

    private fun createColumn(definition: DefinitionModel, difference: DifferenceModel):Boolean {
        if (difference.propertyDefinition != null) {
            return entityService.createColumn(difference.propertyDefinition, definition)
        }
        return false
    }

    private fun dropColumn(definition: DefinitionModel, difference: DifferenceModel):Boolean {
        if (difference.propertyDefinition != null) {
            return entityService.dropColumn(difference.propertyDefinition, definition)
        }
        return false
    }

    private fun createTable(definition: DefinitionModel, difference: DifferenceModel):Boolean {
        return this.entityService.createTable(definition)
    }

    private fun dropTable(definition: DefinitionModel, difference: DifferenceModel):Boolean {
        return this.entityService.dropTable(definition)
    }
}

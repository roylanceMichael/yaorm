package org.roylance.yaorm.utilities

import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.IEntityService
import org.roylance.yaorm.services.ISQLGeneratorService
import java.util.*

object ProjectionUtilities {
    const val FirstAlias = "a"
    const val SecondAlias = "b"

    fun buildTableDefinitionFromProjection(projection: YaormModel.Projection): YaormModel.TableDefinition {
        val returnTableDefinition = YaormModel.TableDefinition.newBuilder().setName(projection.name)

        projection.labelsList.forEach {
            val newColumnDefinition = YaormModel.ColumnDefinition.newBuilder()
            if (it.functionType == YaormModel.SelectFunctionType.NONE_SELECT_FUNCTION_TYPE) {
                newColumnDefinition
                        .setName(it.alias)
                        .setType(it.type)
            }
            else {
                // for now, aggregation types will always be double
                newColumnDefinition
                        .setName(it.alias)
                        .setType(YaormModel.ProtobufType.DOUBLE)
            }
            returnTableDefinition.addColumnDefinitions(newColumnDefinition)
        }

        return returnTableDefinition.build()
    }

    fun buildProjectionSQL(projection: YaormModel.Projection, sqlGeneratorService: ISQLGeneratorService): String {
        val workspace = StringBuilder()

        val columns = projection.labelsList.map {
            if (it.functionType == YaormModel.SelectFunctionType.NONE_SELECT_FUNCTION_TYPE) {
                "\t${it.tableAlias}.${it.name} as ${it.alias}"
            }
            else {
                "\t${it.functionText} as ${it.alias}"
            }
        }

        workspace.appendln("select")
        workspace.appendln(columns.joinToString(",\n"))

        workspace.appendln("${projection.mainTable.name} ${projection.mainTable.alias}")

        projection.joinsList.forEach {
            workspace.appendln("join ${it.secondTable.name} ${it.secondTable.alias}")
            workspace.appendln("\ton ${it.secondTable.alias}.${it.secondColumn.name} = ${it.firstTable.alias}.${it.firstColumn.name}")
        }

        if (projection.hasGroupBy()) {
            workspace.appendln("group by ${projection.groupBy.columnsList.map { it.alias }.joinToString()}")
        }

        if (projection.hasWhereClause()) {
            workspace.appendln(YaormUtils.buildWhereClause(projection.whereClause, sqlGeneratorService))
        }

        val orderBys = projection.orderBysList.map {
            "${it.column.alias} ${it.type.name.toLowerCase()}"
        }

        if (orderBys.isNotEmpty()) {
            workspace.appendln("order by ${orderBys.joinToString()}")
        }

        return workspace.toString()
    }

    fun buildTableDefinitionFromJoinTable(joinTable: YaormModel.JoinTable): YaormModel.TableDefinition {
        val returnTableDefinition = YaormModel.TableDefinition.newBuilder()
                .setName("${joinTable.firstTable.name}_${joinTable.firstColumn.name}_${joinTable.secondTable.name}_${joinTable.secondColumn.name}")

        joinTable.firstTable.columnDefinitionsList.forEach {
            returnTableDefinition.addColumnDefinitions(it.toBuilder().setAlias(FirstAlias))
        }

        joinTable.secondTable.columnDefinitionsList.forEach {
            returnTableDefinition.addColumnDefinitions(it.toBuilder().setAlias(SecondAlias))
        }

        return returnTableDefinition.build()
    }

    fun buildProjectionData(projection: YaormModel.Projection, entityService: IEntityService): YaormModel.Records {
        // first, let's see what type of filter we have for the main table
        val runningRecords = entityService.where(projection.whereClause, projection.mainTable).toBuilder()

        projection.joinsList.forEach { joinTable ->
            // todo: limit by 100 @ a time
            val distinctRecords = HashMap<String, YaormModel.Records.Builder>()

            val distinctValues = HashSet<String>()
            runningRecords.recordsList.forEach { record ->
                val foundColumn = record.columnsList.firstOrNull { column -> column.definition.name == joinTable.firstColumn.name }

                if (foundColumn != null) {
                    val item = YaormUtils.getAnyObject(foundColumn)?.toString()
                    if (item != null) {
                        distinctValues.add(item)

                        if (distinctRecords.containsKey(item)) {
                            distinctRecords[item]!!.addRecords(record)
                        }
                        else {
                            distinctRecords[item] = YaormModel.Records.newBuilder().addRecords(record)
                        }
                    }
                }
            }

            // clear main runningRecords, we're going to build more
            runningRecords.clearRecords()

            val whereClause = YaormModel.WhereClause.newBuilder()
                    .setOperatorType(YaormModel.WhereClause.OperatorType.IN)
                    .setNameAndProperty(YaormModel.Column.newBuilder().setDefinition(joinTable.secondColumn))
                    .addAllInItems(distinctValues)
                    .build()

            val secondTableFilteredRecords = entityService.where(whereClause, joinTable.secondTable)
            secondTableFilteredRecords.recordsList.forEach { record ->
                val foundColumn = record.columnsList.firstOrNull { column -> column.definition.name == joinTable.secondColumn.name }

                if (foundColumn != null) {
                    val item = YaormUtils.getAnyObject(foundColumn)?.toString()
                    if (item != null && distinctRecords.containsKey(item)) {
                        distinctRecords[item]!!.recordsBuilderList.forEach { distinctRecord ->
                            distinctRecord.addAllColumns(record.columnsList)
                        }
                    }
                }
            }

            // how do we merge them with previous runningRecords?
            distinctRecords.values.forEach { distinctRecord ->
                runningRecords.addAllRecords(distinctRecord.recordsList)
            }
        }

        // we need to build up
        if (projection.hasGroupBy()) {
            val customRecords = HashMap<String, YaormModel.Record>()

            runningRecords.recordsList.forEach { record ->
                val groupByHash = ArrayList<String>()
                projection.groupBy.columnsList.forEach { groupByColumn ->
                    val foundColumn = record.columnsList.firstOrNull { it.definition.name == groupByColumn.name }
                    if (foundColumn != null) {
                        val item = YaormUtils.getAnyObject(foundColumn)?.toString()
                        if (item != null) {
                            groupByHash.add(item)
                        }
                        else {
                            groupByHash.add("")
                        }
                    }
                }

                val hash = groupByHash.joinToString()
                customRecords[hash] = record
            }

            return YaormModel.Records.newBuilder().addAllRecords(customRecords.values).build()
        }

        return runningRecords.build()
    }
}
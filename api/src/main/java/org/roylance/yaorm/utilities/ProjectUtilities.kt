package org.roylance.yaorm.utilities

import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.ISQLGeneratorService

object ProjectUtilities {
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
}
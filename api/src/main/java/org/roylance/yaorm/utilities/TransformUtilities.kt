package org.roylance.yaorm.utilities

import org.roylance.yaorm.YaormModel

object TransformUtilities {
    // need or in here
    fun filter(records: YaormModel.Records, whereClause: YaormModel.WhereClause): YaormModel.Records {
        val returnRecords = YaormModel.Records.newBuilder()

        records.recordsList.forEach { record ->
            var allCorrect = true
            var currentWhereClause: YaormModel.WhereClause? = whereClause

            while (currentWhereClause != null) {
                val whereClauseResult = verifyWhereClause(record, currentWhereClause)

                if (!whereClauseResult) {
                    allCorrect = false
                   break
                }

                if (currentWhereClause.hasConnectingWhereClause()) {
                    currentWhereClause = currentWhereClause.connectingWhereClause
                }
                else {
                    currentWhereClause = null
                }
            }

            if (allCorrect) {
                returnRecords.addRecords(record)
            }
        }

        return returnRecords.build()
    }

    fun merge(firstRecords: YaormModel.Records,
              secondRecords: YaormModel.Records,
              joinTable: YaormModel.JoinTable): YaormModel.TableRecords {



        return YaormModel.TableRecords.getDefaultInstance()
    }

    fun aggregate(records: YaormModel.Records): YaormModel.TableRecords {
        return YaormModel.TableRecords.getDefaultInstance()
    }

    fun project(records: YaormModel.Records): YaormModel.TableRecords {
        return YaormModel.TableRecords.getDefaultInstance()
    }

    private fun verifyWhereClause(record: YaormModel.Record, whereClause: YaormModel.WhereClause): Boolean {
        val comparableColumn = record.columnsList.firstOrNull { it.definition.name == whereClause.nameAndProperty.definition.name }
                ?: return false

        if (whereClause.operatorType == YaormModel.WhereClause.OperatorType.EQUALS) {
            val recordItem = YaormUtils.getAnyObject(comparableColumn)
            val whereClauseItem = YaormUtils.getAnyObject(whereClause.nameAndProperty)
            return recordItem == whereClauseItem
        }
        if (whereClause.operatorType == YaormModel.WhereClause.OperatorType.NOT_EQUALS) {
            val recordItem = YaormUtils.getAnyObject(comparableColumn)
            val whereClauseItem = YaormUtils.getAnyObject(whereClause.nameAndProperty)
            return recordItem != whereClauseItem
        }

        if (whereClause.operatorType == YaormModel.WhereClause.OperatorType.IN) {
            val recordItem = YaormUtils.getAnyObject(comparableColumn)?.toString() ?: return false
            return whereClause.inItemsList.any { it == recordItem }
        }

        if (comparableColumn.definition.type == YaormModel.ProtobufType.STRING ||
                comparableColumn.definition.type == YaormModel.ProtobufType.BYTES) {
            return false
        }

        var recordItem: Double = 0.0
        var whereClauseItem: Double = 0.0

        if (comparableColumn.definition.type == YaormModel.ProtobufType.DOUBLE) {
            recordItem = comparableColumn.doubleHolder
            whereClauseItem = whereClause.nameAndProperty.doubleHolder
        }
        else if (comparableColumn.definition.type == YaormModel.ProtobufType.FLOAT) {
            recordItem = comparableColumn.floatHolder.toDouble()
            whereClauseItem = whereClause.nameAndProperty.floatHolder.toDouble()
        }
        else {
            val tempRecordItem = YaormUtils.getAnyObject(comparableColumn)?.toString()
            val tempWhereClauseItem = YaormUtils.getAnyObject(whereClause.nameAndProperty)?.toString()

            if (tempRecordItem != null && tempWhereClauseItem != null) {
                recordItem = tempRecordItem.toDouble()
                whereClauseItem = tempWhereClauseItem.toDouble()
            }
        }

        if (whereClause.operatorType == YaormModel.WhereClause.OperatorType.GREATER_THAN) {
            return recordItem == whereClauseItem
        }
        else if (whereClause.operatorType == YaormModel.WhereClause.OperatorType.LESS_THAN) {
            return whereClauseItem < recordItem
        }
        else if (whereClause.operatorType == YaormModel.WhereClause.OperatorType.GREATER_THAN) {
            return whereClauseItem > recordItem
        }
        return false
    }
}
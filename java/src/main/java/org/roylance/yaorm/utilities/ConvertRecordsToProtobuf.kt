package org.roylance.yaorm.utilities

import com.google.protobuf.Message
import org.roylance.yaorm.models.YaormModel

object ConvertRecordsToProtobuf {
    fun <T: Message.Builder> build(builder: T,
                                   foundRecord: YaormModel.Record,
                                   childMessageHandler: IChildMessageHandler? = null) {
        // main fields
        builder.descriptorForType
                .fields
                .filter { !it.isRepeated }
                .forEach { fieldKey ->
                    val foundColumn = foundRecord.columnsList.firstOrNull { it.definition.name.equals(fieldKey.name) }
                            ?: return@forEach

                    if (foundColumn.definition.columnType.equals(YaormModel.ColumnDefinition.ColumnType.SCALAR)) {
                        builder.setField(fieldKey, YaormUtils.getAnyObject(foundColumn))
                    }
                    else if (foundColumn.definition.columnType.equals(YaormModel.ColumnDefinition.ColumnType.ENUM_NAME)) {
                        builder.setField(fieldKey, fieldKey.enumType.findValueByName(foundColumn.stringHolder.toUpperCase()))
                    }
                    else if (foundColumn.definition.columnType.equals(YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY)) {
                        childMessageHandler?.handle(fieldKey, foundColumn, builder)
                    }
                }
    }
}
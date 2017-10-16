package org.roylance.yaorm.utilities

import com.google.protobuf.Message
import org.roylance.yaorm.YaormModel

object ConvertRecordsToProtobuf {
  fun <T : Message.Builder> build(builder: T,
      foundRecord: YaormModel.Record,
      childMessageHandler: IChildMessageHandler? = null) {
    // main fields
    builder.descriptorForType
        .fields
        .filter { !it.isRepeated }
        .forEach { fieldKey ->
          val foundColumn = foundRecord.columnsList.firstOrNull { it.definition.name == fieldKey.name }
              ?: return@forEach

          if (foundColumn.definition.columnType == YaormModel.ColumnDefinition.ColumnType.SCALAR) {
            builder.setField(fieldKey, YaormUtils.getAnyObject(foundColumn))
          } else if (foundColumn.definition.columnType == YaormModel.ColumnDefinition.ColumnType.ENUM_NAME) {
            if (foundColumn.stringHolder != null) {
              val foundValue = fieldKey.enumType.values
                  .firstOrNull { it.name == foundColumn.stringHolder.toUpperCase() }
              if (foundValue != null) {
                builder.setField(fieldKey, foundValue)
              }
            }
          } else if (foundColumn.definition.columnType == YaormModel.ColumnDefinition.ColumnType.MESSAGE_KEY) {
            childMessageHandler?.handle(fieldKey, foundColumn, builder)
          }
        }
  }
}
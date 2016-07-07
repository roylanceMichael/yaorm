package org.roylance.yaorm.models.db

import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.YaormModel

class GenericModel(
        override var id: String = "",
        var strVal: String = "",
        var intVal: Int = 0,
        var longVal: Long = 0L,
        var doubleVal: Double = 0.0,
        var boolVal: Boolean = false) : IEntity {
    companion object {
        const val IdName = "id"
        const val StrValName = "strVal"
        const val IntValName = "intVal"
        const val LongValName = "longVal"
        const val DoubleValName = "doubleVal"
        const val BoolValName = "boolVal"

        fun buildProtoDefinitionModel():YaormModel.TableDefinition {
            val returnModel = YaormModel.TableDefinition.newBuilder()
            returnModel.mutableColumnDefinitions[IdName] = YaormModel.ColumnDefinition.newBuilder().setName(IdName).setType(YaormModel.ProtobufType.STRING).build()
            returnModel.mutableColumnDefinitions[StrValName] = (YaormModel.ColumnDefinition.newBuilder().setName(StrValName).setType(YaormModel.ProtobufType.STRING)).build()
            returnModel.mutableColumnDefinitions[IntValName] = (YaormModel.ColumnDefinition.newBuilder().setName(IntValName).setType(YaormModel.ProtobufType.INT64)).build()
            returnModel.mutableColumnDefinitions[LongValName] = (YaormModel.ColumnDefinition.newBuilder().setName(LongValName).setType(YaormModel.ProtobufType.INT64)).build()
            returnModel.mutableColumnDefinitions[DoubleValName] = (YaormModel.ColumnDefinition.newBuilder().setName(DoubleValName).setType(YaormModel.ProtobufType.DOUBLE)).build()
            returnModel.mutableColumnDefinitions[BoolValName] = (YaormModel.ColumnDefinition.newBuilder().setName(BoolValName).setType(YaormModel.ProtobufType.BOOL)).build()
            return returnModel.build()
        }
    }
}

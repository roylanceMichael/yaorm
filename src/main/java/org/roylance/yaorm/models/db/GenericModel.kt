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

        fun buildProtoDefinitionModel():YaormModel.Definition {
            val returnModel = YaormModel.Definition.newBuilder()
            returnModel.addPropertyDefinitions(YaormModel.PropertyDefinition.newBuilder().setName(IdName).setType(YaormModel.ProtobufType.STRING))
            returnModel.addPropertyDefinitions(YaormModel.PropertyDefinition.newBuilder().setName(StrValName).setType(YaormModel.ProtobufType.STRING))
            returnModel.addPropertyDefinitions(YaormModel.PropertyDefinition.newBuilder().setName(IntValName).setType(YaormModel.ProtobufType.INT64))
            returnModel.addPropertyDefinitions(YaormModel.PropertyDefinition.newBuilder().setName(LongValName).setType(YaormModel.ProtobufType.INT64))
            returnModel.addPropertyDefinitions(YaormModel.PropertyDefinition.newBuilder().setName(DoubleValName).setType(YaormModel.ProtobufType.DOUBLE))
            returnModel.addPropertyDefinitions(YaormModel.PropertyDefinition.newBuilder().setName(BoolValName).setType(YaormModel.ProtobufType.BOOL))
            return returnModel.build()
        }
    }
}

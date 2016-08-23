package org.roylance.yaorm.utilities

import com.google.protobuf.GeneratedMessageV3
import org.roylance.yaorm.ComplexModel
import org.roylance.yaorm.services.proto.BaseProtoGeneratedMessageBuilder
import org.roylance.yaorm.services.proto.IProtoGeneratedMessageBuilder

object ComplexModelBuilder: BaseProtoGeneratedMessageBuilder(), IProtoGeneratedMessageBuilder {
    override val name: String
        get() = "ComplexModel"

    override fun buildGeneratedMessage(name: String): GeneratedMessageV3 {
        if (ComplexModel.Beacon.getDescriptor().name.equals(name)) {
            return ComplexModel.Beacon.getDefaultInstance()
        }
        if (ComplexModel.ClientBeacon.getDescriptor().name.equals(name)) {
            return ComplexModel.ClientBeacon.getDefaultInstance()
        }
        if (ComplexModel.RequestImage.getDescriptor().name.equals(name)) {
            return ComplexModel.RequestImage.getDefaultInstance()
        }
        if (ComplexModel.ExpectedAnswer.getDescriptor().name.equals(name)) {
            return ComplexModel.ExpectedAnswer.getDefaultInstance()
        }
        if (ComplexModel.Question.getDescriptor().name.equals(name)) {
            return ComplexModel.Question.getDefaultInstance()
        }
        if (ComplexModel.Validation.getDescriptor().name.equals(name)) {
            return ComplexModel.Validation.getDefaultInstance()
        }
        if (ComplexModel.HeuristicCombiner.getDescriptor().name.equals(name)) {
            return ComplexModel.HeuristicCombiner.getDefaultInstance()
        }
        if (ComplexModel.Form.getDescriptor().name.equals(name)) {
            return ComplexModel.Form.getDefaultInstance()
        }
        if (ComplexModel.View.getDescriptor().name.equals(name)) {
            return ComplexModel.View.getDefaultInstance()
        }
        if (ComplexModel.Heuristic.getDescriptor().name.equals(name)) {
            return ComplexModel.Heuristic.getDefaultInstance()
        }
        if (ComplexModel.Request.getDescriptor().name.equals(name)) {
            return ComplexModel.Request.getDefaultInstance()
        }
        if (ComplexModel.Response.getDescriptor().name.equals(name)) {
            return ComplexModel.Response.getDefaultInstance()
        }
        if (ComplexModel.Answer.getDescriptor().name.equals(name)) {
            return ComplexModel.Answer.getDefaultInstance()
        }

        return super.buildGeneratedMessage(name)
    }
}
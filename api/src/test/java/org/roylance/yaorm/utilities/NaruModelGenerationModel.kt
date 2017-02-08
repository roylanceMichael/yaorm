package org.roylance.yaorm.utilities

import  com.google.protobuf.GeneratedMessageV3
import  org.roylance.yaorm.services.proto.BaseProtoGeneratedMessageBuilder

object  NaruModelGenerationModel: BaseProtoGeneratedMessageBuilder() {
    override val name: String
        get() = "NaruModelModel"

    override fun buildGeneratedMessage(name: String): GeneratedMessageV3 {
        if (org.naru.NaruModel.Beacon.getDescriptor().name == name) {
            return org.naru.NaruModel.Beacon.getDefaultInstance()
        }
        if (org.naru.NaruModel.RequestImage.getDescriptor().name == name) {
            return org.naru.NaruModel.RequestImage.getDefaultInstance()
        }
        if (org.naru.NaruModel.ExpectedAnswer.getDescriptor().name == name) {
            return org.naru.NaruModel.ExpectedAnswer.getDefaultInstance()
        }
        if (org.naru.NaruModel.Question.getDescriptor().name == name) {
            return org.naru.NaruModel.Question.getDefaultInstance()
        }
        if (org.naru.NaruModel.QuestionAnswer.getDescriptor().name == name) {
            return org.naru.NaruModel.QuestionAnswer.getDefaultInstance()
        }
        if (org.naru.NaruModel.FormQuestions.getDescriptor().name == name) {
            return org.naru.NaruModel.FormQuestions.getDefaultInstance()
        }
        if (org.naru.NaruModel.FormQuestionsGroup.getDescriptor().name == name) {
            return org.naru.NaruModel.FormQuestionsGroup.getDefaultInstance()
        }
        if (org.naru.NaruModel.Order.getDescriptor().name == name) {
            return org.naru.NaruModel.Order.getDefaultInstance()
        }
        if (org.naru.NaruModel.Validation.getDescriptor().name == name) {
            return org.naru.NaruModel.Validation.getDefaultInstance()
        }
        if (org.naru.NaruModel.HeuristicCombiner.getDescriptor().name == name) {
            return org.naru.NaruModel.HeuristicCombiner.getDefaultInstance()
        }
        if (org.naru.NaruModel.Form.getDescriptor().name == name) {
            return org.naru.NaruModel.Form.getDefaultInstance()
        }
        if (org.naru.NaruModel.View.getDescriptor().name == name) {
            return org.naru.NaruModel.View.getDefaultInstance()
        }
        if (org.naru.NaruModel.Heuristic.getDescriptor().name == name) {
            return org.naru.NaruModel.Heuristic.getDefaultInstance()
        }
        if (org.naru.NaruModel.Request.getDescriptor().name == name) {
            return org.naru.NaruModel.Request.getDefaultInstance()
        }
        if (org.naru.NaruModel.Organization.getDescriptor().name == name) {
            return org.naru.NaruModel.Organization.getDefaultInstance()
        }
        if (org.naru.NaruModel.OrganizationUser.getDescriptor().name == name) {
            return org.naru.NaruModel.OrganizationUser.getDefaultInstance()
        }
        if (org.naru.NaruModel.ProductDetail.getDescriptor().name == name) {
            return org.naru.NaruModel.ProductDetail.getDefaultInstance()
        }
        if (org.naru.NaruModel.UINaruRequest.getDescriptor().name == name) {
            return org.naru.NaruModel.UINaruRequest.getDefaultInstance()
        }
        if (org.naru.NaruModel.UINaruResponse.getDescriptor().name == name) {
            return org.naru.NaruModel.UINaruResponse.getDefaultInstance()
        }
        if (org.naru.NaruModel.UINaruAdminRequest.getDescriptor().name == name) {
            return org.naru.NaruModel.UINaruAdminRequest.getDefaultInstance()
        }
        if (org.naru.NaruModel.UINaruAdminResponse.getDescriptor().name == name) {
            return org.naru.NaruModel.UINaruAdminResponse.getDefaultInstance()
        }
        return super.buildGeneratedMessage(name)
    }
}
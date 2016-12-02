package org.roylance.yaorm.utilities

import com.google.protobuf.GeneratedMessageV3
import org.roylance.yaorm.NestedEnumTest
import org.roylance.yaorm.services.proto.BaseProtoGeneratedMessageBuilder

class NestedEnumGMBuilder: BaseProtoGeneratedMessageBuilder() {
    override val name: String
        get() = "NestedEnumTest"

    override fun buildGeneratedMessage(name: String): GeneratedMessageV3 {
        if (NestedEnumTest.Customer.getDescriptor().name == name) {
            return NestedEnumTest.Customer.getDefaultInstance()
        }
        else if (NestedEnumTest.ConnectionInformation.getDescriptor().name == name) {
            return NestedEnumTest.ConnectionInformation.getDefaultInstance()
        }
        else if (NestedEnumTest.Projection.getDescriptor().name == name) {
            return NestedEnumTest.Projection.getDefaultInstance()
        }
        else if (NestedEnumTest.ProjectionTable.getDescriptor().name == name) {
            return NestedEnumTest.ProjectionTable.getDefaultInstance()
        }
        else if (NestedEnumTest.Transformation.getDescriptor().name == name) {
            return NestedEnumTest.Transformation.getDefaultInstance()
        }
        else if (NestedEnumTest.Input.getDescriptor().name == name) {
            return NestedEnumTest.Input.getDefaultInstance()
        }
        else if (NestedEnumTest.Output.getDescriptor().name == name) {
            return NestedEnumTest.Output.getDefaultInstance()
        }
        else if (NestedEnumTest.DataSet.getDescriptor().name == name) {
            return NestedEnumTest.DataSet.getDefaultInstance()
        }
        else if (NestedEnumTest.ReceivedDataSet.getDescriptor().name == name) {
            return NestedEnumTest.ReceivedDataSet.getDefaultInstance()
        }
        else if (NestedEnumTest.ColumnInfo.getDescriptor().name == name) {
            return NestedEnumTest.ColumnInfo.getDefaultInstance()
        }
        else if (NestedEnumTest.MySQLRedshiftTable.getDescriptor().name == name) {
            return NestedEnumTest.MySQLRedshiftTable.getDefaultInstance()
        }
        else if (NestedEnumTest.RegularExpression.getDescriptor().name == name) {
            return NestedEnumTest.RegularExpression.getDefaultInstance()
        }
        else if (NestedEnumTest.Validation.getDescriptor().name == name) {
            return NestedEnumTest.Validation.getDefaultInstance()
        }
        else if (NestedEnumTest.OutputValidation.getDescriptor().name == name) {
            return NestedEnumTest.OutputValidation.getDefaultInstance()
        }
        else if (NestedEnumTest.ValidationReport.getDescriptor().name == name) {
            return NestedEnumTest.ValidationReport.getDefaultInstance()
        }
        else if (NestedEnumTest.DataWarehouseColumn.getDescriptor().name == name) {
            return NestedEnumTest.DataWarehouseColumn.getDefaultInstance()
        }
        else if (NestedEnumTest.DataWarehouseColumnValidation.getDescriptor().name == name) {
            return NestedEnumTest.DataWarehouseColumnValidation.getDefaultInstance()
        }
        else if (NestedEnumTest.DataWarehouseTable.getDescriptor().name == name) {
            return NestedEnumTest.DataWarehouseTable.getDefaultInstance()
        }
        else if (NestedEnumTest.DataWarehouseTableColumn.getDescriptor().name == name) {
            return NestedEnumTest.DataWarehouseTableColumn.getDefaultInstance()
        }
        else if (NestedEnumTest.Mapping.getDescriptor().name == name) {
            return NestedEnumTest.Mapping.getDefaultInstance()
        }
        else if (NestedEnumTest.DataSetTemplate.getDescriptor().name == name) {
            return NestedEnumTest.DataSetTemplate.getDefaultInstance()
        }
        else if (NestedEnumTest.RestCustomers.getDescriptor().name == name) {
            return NestedEnumTest.RestCustomers.getDefaultInstance()
        }
        else if (NestedEnumTest.RestDataWarehouseInfo.getDescriptor().name == name) {
            return NestedEnumTest.RestDataWarehouseInfo.getDefaultInstance()
        }
        else if (NestedEnumTest.ReportEligibility.getDescriptor().name == name) {
            return NestedEnumTest.ReportEligibility.getDefaultInstance()
        }
        else if (NestedEnumTest.ClaimsTriangle.getDescriptor().name == name) {
            return NestedEnumTest.ClaimsTriangle.getDefaultInstance()
        }
        else if (NestedEnumTest.ServiceCategory.getDescriptor().name == name) {
            return NestedEnumTest.ServiceCategory.getDefaultInstance()
        }

        return super.buildGeneratedMessage(name)
    }
}
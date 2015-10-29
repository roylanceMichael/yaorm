package org.roylance.yaorm.models.migration

public class DifferenceReportModel(
        public val migrationExists: Boolean,
        public val differences: List<DifferenceModel>)

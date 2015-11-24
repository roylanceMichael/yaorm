package org.roylance.yaorm.models.migration

class DifferenceReportModel(
    val migrationExists: Boolean,
    val differences: List<DifferenceModel>) {
    fun differenceExists():Boolean {
        return this.differences.size > 0
    }
}

package org.roylance.yaorm.jvm.sqlite

import org.roylance.yaorm.YaormModel
import org.roylance.yaorm.services.ISqlGeneratorService

class SQLiteGeneratorService : ISqlGeneratorService {
    override fun blob_type_name(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        return YaormModel.SqlGeneratorRequestResponse.newBuilder().setResponse(SqlBlobName).build()
    }

    override fun build_bulk_insert(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun build_count_sql(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun build_create_column(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun build_create_index(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun build_create_table(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun build_delete_all(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun build_delete_table(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun build_delete_with_criteria(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun build_drop_column(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun build_drop_index(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun build_drop_table(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun build_insert(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun build_select_all(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun build_select_ids(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun build_table_definition(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun build_table_definition_sql(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun build_update(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun build_update_with_criteria(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun build_where_clause(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get_schema_names(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get_table_names(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun integer_type_name(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun proto_type_to_sql_type(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun real_type_name(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sql_type_to_proto_type(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun text_type_name(request: YaormModel.SqlGeneratorRequestResponse): YaormModel.SqlGeneratorRequestResponse {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private const val Empty = ""
        private const val DoubleQuote = "\""
        private const val Comma = ","
        private const val Space = " "

        private const val CreateInitialTableTemplate = "create table if not exists %s (%s);"
        private const val InsertIntoTableSingleTemplate = "replace into %s (%s) values (%s);"
        private const val UpdateTableSingleTemplate = "update %s set %s where id=%s;"
        private const val DeleteTableTemplate = "delete from %s where \"id\"=%s;"
        private const val WhereClauseTemplate = "select * from %s where %s;"
        private const val SelectAllTemplate = "select * from %s limit %s offset %s;"
        private const val PrimaryKey = "primary key"

        private const val SqlIntegerName = "integer"
        private const val SqlTextName = "text"
        private const val SqlRealName = "real"
        private const val SqlBlobName = "text"

        private const val SchemaTableRegexStr = """CREATE TABLE "(.+)" \((.+)\)"""
        private val SchemaTableRegex = Regex(SchemaTableRegexStr)
    }
}
// THIS FILE WAS AUTO-GENERATED. DO NOT ALTER!
package org.roylance.yaorm.rest;

import org.roylance.common.service.IProtoSerializerService;

import org.roylance.yaorm.utilities.ServiceLocator;
import org.roylance.yaorm.services.ISqlGeneratorService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;

@Path("/sqlgenerator")
public class SqlGeneratorController {
    @Context
    private ServletContext context;
    @Context
    private HttpServletRequest request;
    @Context
    private HttpServletResponse response;

    private final ISqlGeneratorService sqlGeneratorService;
    private final IProtoSerializerService serializerService;

    public SqlGeneratorController() {
        this.serializerService = ServiceLocator.INSTANCE.getProtobufSerializerService();
        this.sqlGeneratorService = ServiceLocator.INSTANCE.getSqlGeneratorService();
    }

    @POST
    @Path("/proto-type-to-sql-type")
    public void proto_type_to_sql_type(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.proto_type_to_sql_type(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/sql-type-to-proto-type")
    public void sql_type_to_proto_type(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.sql_type_to_proto_type(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/text-type-name")
    public void text_type_name(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.text_type_name(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/integer-type-name")
    public void integer_type_name(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.integer_type_name(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/real-type-name")
    public void real_type_name(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.real_type_name(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/blob-type-name")
    public void blob_type_name(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.blob_type_name(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/build-count-sql")
    public void build_count_sql(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.build_count_sql(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/build-create-column")
    public void build_create_column(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.build_create_column(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/build-drop-column")
    public void build_drop_column(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.build_drop_column(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/build-create-index")
    public void build_create_index(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.build_create_index(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/build-drop-index")
    public void build_drop_index(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.build_drop_index(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/build-create-table")
    public void build_create_table(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.build_create_table(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/build-drop-table")
    public void build_drop_table(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.build_drop_table(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/build-delete-all")
    public void build_delete_all(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.build_delete_all(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/build-delete-table")
    public void build_delete_table(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.build_delete_table(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/build-delete-with-criteria")
    public void build_delete_with_criteria(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.build_delete_with_criteria(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/build-bulk-insert")
    public void build_bulk_insert(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.build_bulk_insert(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/build-insert")
    public void build_insert(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.build_insert(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/build-update")
    public void build_update(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.build_update(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/build-update-with-criteria")
    public void build_update_with_criteria(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.build_update_with_criteria(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/build-select-all")
    public void build_select_all(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.build_select_all(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/build-where-clause")
    public void build_where_clause(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.build_where_clause(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/build-select-ids")
    public void build_select_ids(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.build_select_ids(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/get-schema-names")
    public void get_schema_names(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.get_schema_names(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/get-table-names")
    public void get_table_names(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.get_table_names(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/build-table-definition-sql")
    public void build_table_definition_sql(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.build_table_definition_sql(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/build-table-definition")
    public void build_table_definition(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.SqlGeneratorRequestResponse response = this.sqlGeneratorService.build_table_definition(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }
}
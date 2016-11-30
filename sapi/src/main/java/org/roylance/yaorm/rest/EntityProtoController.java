// THIS FILE WAS AUTO-GENERATED. DO NOT ALTER!
package org.roylance.yaorm.rest;

import org.roylance.common.service.IProtoSerializerService;

import org.roylance.yaorm.utilities.ServiceLocator;
import org.roylance.yaorm.services.IEntityProtoService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;

@Path("/entityproto")
public class EntityProtoController {
    @Context
    private ServletContext context;
    @Context
    private HttpServletRequest request;
    @Context
    private HttpServletResponse response;

    private final IEntityProtoService entityProtoService;
    private final IProtoSerializerService serializerService;

    public EntityProtoController() {
        this.serializerService = ServiceLocator.INSTANCE.getProtobufSerializerService();
        this.entityProtoService = ServiceLocator.INSTANCE.getEntityProtoService();
    }

    @POST
    @Path("/create-table")
    public void create_table(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.create_table(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/drop-table")
    public void drop_table(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.drop_table(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/create-index")
    public void create_index(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.create_index(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/drop-index")
    public void drop_index(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.drop_index(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/create-column")
    public void create_column(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.create_column(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/drop-column")
    public void drop_column(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.drop_column(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/bulk-insert")
    public void bulk_insert(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.bulk_insert(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/create-or-update")
    public void create_or_update(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.create_or_update(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/create")
    public void create(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.create(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/update")
    public void update(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.update(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/update-with-criteria")
    public void update_with_criteria(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.update_with_criteria(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/update-custom")
    public void update_custom(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.update_custom(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/delete")
    public void delete(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.delete(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/delete-all")
    public void delete_all(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.delete_all(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/get-count")
    public void get_count(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.get_count(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/get")
    public void get(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.get(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/get-ids")
    public void get_ids(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.get_ids(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/get-custom")
    public void get_custom(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.get_custom(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/get-many")
    public void get_many(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.get_many(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/where")
    public void where(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.where(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/build-definition-from-sql")
    public void build_definition_from_sql(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.build_definition_from_sql(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/get-schema-names")
    public void get_schema_names(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.get_schema_names(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/get-table-names")
    public void get_table_names(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.get_table_names(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/get-table-definition")
    public void get_table_definition(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityProtoService.get_table_definition(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }
}
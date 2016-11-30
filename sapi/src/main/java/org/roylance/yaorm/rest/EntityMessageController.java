// THIS FILE WAS AUTO-GENERATED. DO NOT ALTER!
package org.roylance.yaorm.rest;

import org.roylance.common.service.IProtoSerializerService;

import org.roylance.yaorm.utilities.ServiceLocator;
import org.roylance.yaorm.services.IEntityMessageService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;

@Path("/entitymessage")
public class EntityMessageController {
    @Context
    private ServletContext context;
    @Context
    private HttpServletRequest request;
    @Context
    private HttpServletResponse response;

    private final IEntityMessageService entityMessageService;
    private final IProtoSerializerService serializerService;

    public EntityMessageController() {
        this.serializerService = ServiceLocator.INSTANCE.getProtobufSerializerService();
        this.entityMessageService = ServiceLocator.INSTANCE.getEntityMessageService();
    }

    @POST
    @Path("/create-entire-schema")
    public void create_entire_schema(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityMessageService.create_entire_schema(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/drop-and-create-entire-schema")
    public void drop_and_create_entire_schema(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityMessageService.drop_and_create_entire_schema(requestActual);
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

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityMessageService.bulk_insert(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/merge")
    public void merge(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityMessageService.merge(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/merge-table")
    public void merge_table(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityMessageService.merge_table(requestActual);
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

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityMessageService.delete(requestActual);
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

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityMessageService.get(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/get-keys")
    public void get_keys(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityMessageService.get_keys(requestActual);
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

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityMessageService.get_many(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/get-single-level")
    public void get_single_level(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityMessageService.get_single_level(requestActual);
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

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityMessageService.where(requestActual);
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

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityMessageService.get_count(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/get-custom-single-level")
    public void get_custom_single_level(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.entityMessageService.get_custom_single_level(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }
}
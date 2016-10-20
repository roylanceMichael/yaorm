// THIS FILE WAS AUTO-GENERATED. DO NOT ALTER!
package org.roylance.yaorm.rest;

import org.roylance.common.service.IProtoSerializerService;

import org.roylance.yaorm.utilities.ServiceLocator;
import org.roylance.yaorm.services.IYaormMainService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;

@Path("/yaormmain")
public class YaormMainController {
    @Context
    private ServletContext context;
    @Context
    private HttpServletRequest request;
    @Context
    private HttpServletResponse response;

    private final IYaormMainService yaormMainService;
    private final IProtoSerializerService serializerService;

    public YaormMainController() {
        this.serializerService = ServiceLocator.INSTANCE.getProtobufSerializerService();
        this.yaormMainService = ServiceLocator.INSTANCE.getYaormMainService();
    }

    @POST
    @Path("/get-schemas")
    public void get_schemas(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.yaormMainService.get_schemas(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/get-tables")
    public void get_tables(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.yaormMainService.get_tables(requestActual);
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

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.yaormMainService.get_table_definition(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/get-table-definitions")
    public void get_table_definitions(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.yaormMainService.get_table_definitions(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/get-record-count")
    public void get_record_count(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.yaormMainService.get_record_count(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/get-records")
    public void get_records(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.yaormMainService.get_records(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }
}
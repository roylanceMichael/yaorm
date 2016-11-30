// THIS FILE WAS AUTO-GENERATED. DO NOT ALTER!
package org.roylance.yaorm.rest;

import org.roylance.common.service.IProtoSerializerService;

import org.roylance.yaorm.utilities.ServiceLocator;
import org.roylance.yaorm.services.IGranularDatabaseService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;

@Path("/granulardatabase")
public class GranularDatabaseController {
    @Context
    private ServletContext context;
    @Context
    private HttpServletRequest request;
    @Context
    private HttpServletResponse response;

    private final IGranularDatabaseService granularDatabaseService;
    private final IProtoSerializerService serializerService;

    public GranularDatabaseController() {
        this.serializerService = ServiceLocator.INSTANCE.getProtobufSerializerService();
        this.granularDatabaseService = ServiceLocator.INSTANCE.getGranularDatabaseService();
    }

    @POST
    @Path("/is-available")
    public void is_available(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.granularDatabaseService.is_available(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/execute-update-query")
    public void execute_update_query(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.granularDatabaseService.execute_update_query(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/build-table-definition-from-query")
    public void build_table_definition_from_query(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.granularDatabaseService.build_table_definition_from_query(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/execute-select-query")
    public void execute_select_query(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.granularDatabaseService.execute_select_query(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/commit")
    public void commit(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.granularDatabaseService.commit(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }

    @POST
    @Path("/get-report")
    public void get_report(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.granularDatabaseService.get_report(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }
}
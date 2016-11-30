// THIS FILE WAS AUTO-GENERATED. DO NOT ALTER!
package org.roylance.yaorm.rest;

import org.roylance.common.service.IProtoSerializerService;

import org.roylance.yaorm.utilities.ServiceLocator;
import org.roylance.yaorm.services.ICursorService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;

@Path("/cursor")
public class CursorController {
    @Context
    private ServletContext context;
    @Context
    private HttpServletRequest request;
    @Context
    private HttpServletResponse response;

    private final ICursorService cursorService;
    private final IProtoSerializerService serializerService;

    public CursorController() {
        this.serializerService = ServiceLocator.INSTANCE.getProtobufSerializerService();
        this.cursorService = ServiceLocator.INSTANCE.getCursorService();
    }

    @POST
    @Path("/get-records")
    public void get_records(@Suspended AsyncResponse asyncResponse, String request) throws Exception {
        new Thread(() -> {
            
            final org.roylance.yaorm.YaormModel.UIYaormRequest requestActual =
                    this.serializerService.deserializeFromBase64(request, org.roylance.yaorm.YaormModel.UIYaormRequest.getDefaultInstance());

            final org.roylance.yaorm.YaormModel.UIYaormResponse response = this.cursorService.get_records(requestActual);
            final String deserializeResponse = this.serializerService.serializeToBase64(response);
            asyncResponse.resume(deserializeResponse);

        }).start();
    }
}
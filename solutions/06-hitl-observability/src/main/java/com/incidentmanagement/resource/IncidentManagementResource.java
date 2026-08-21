package com.incidentmanagement.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;

import com.incidentmanagement.service.IncidentManagementService;

@Path("/incident-management")
public class IncidentManagementResource {

    @Inject
    IncidentManagementService incidentManagementService;

    @POST
    @Path("/process/{incidentNumber}")
    @Blocking
    public Uni<String> processIncident(@RestPath Integer incidentNumber, @RestQuery @DefaultValue("") String feedback) {
        return incidentManagementService.processIncident(incidentNumber, feedback);
    }

    @GET
    @Path("/report")
    @Produces(MediaType.TEXT_HTML)
    public String report() {
        return incidentManagementService.report();
    }

    @ServerExceptionMapper
    public RestResponse<String> mapException(Exception e) {
        Log.error(e.getMessage(), e);
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, "Error processing incident: " + e.getMessage());
    }
}

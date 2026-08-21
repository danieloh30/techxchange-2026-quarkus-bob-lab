package com.incidentmanagement.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import io.quarkus.logging.Log;

import com.incidentmanagement.service.IncidentManagementService;

// --8<-- [start:incident-management]
@Path("/incident-management")
public class IncidentManagementResource {

    @Inject
    IncidentManagementService incidentManagementService;

    @POST
    @Path("/process/{incidentNumber}")
    public String processIncident(Integer incidentNumber, @RestQuery @DefaultValue("") String report) {
        return incidentManagementService.processIncident(incidentNumber, report);
    }

    @ServerExceptionMapper
    public RestResponse<String> mapException(Exception e) {
        Log.error(e.getMessage(), e);
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, "Error processing incident: " + e.getMessage());
    }
}
// --8<-- [end:incident-management]

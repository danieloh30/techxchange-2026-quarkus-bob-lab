package com.incidentmanagement.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import dev.langchain4j.data.message.ImageContent;
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
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Blocking
    public Uni<String> processIncident(@RestPath Integer incidentNumber, @RestForm @DefaultValue("") String report, @RestForm FileUpload logImage) {
        ImageContent imageContent = toImageContent(logImage);
        return incidentManagementService.processIncident(incidentNumber, report, imageContent);
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

    private ImageContent toImageContent(FileUpload fileUpload) {
        if (fileUpload == null || fileUpload.filePath() == null) {
            return null;
        }
        try {
            byte[] bytes = Files.readAllBytes(fileUpload.filePath());
            String base64 = Base64.getEncoder().encodeToString(bytes);
            String mimeType = fileUpload.contentType();
            return new ImageContent(base64, mimeType);
        } catch (IOException e) {
            Log.error("Failed to read uploaded log image", e);
            return null;
        }
    }
}

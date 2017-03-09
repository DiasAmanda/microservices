package br.com.amanda.matera.edge.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("services/healthcheck")
public class HealthcheckRS {

    private final Logger LOG = LoggerFactory.getLogger(HealthcheckRS.class);

    @GET
    public Response getHealthcheck() {

        LOG.debug("Attempting healthcheck status");

        return Response.ok().build();
    }

}

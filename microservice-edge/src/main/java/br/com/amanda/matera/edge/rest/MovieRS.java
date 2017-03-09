package br.com.amanda.matera.edge.rest;

import br.com.amanda.matera.core.domain.Movie;
import br.com.amanda.matera.edge.filter.MovieFilter;
import br.com.amanda.matera.edge.service.MovieService;
import com.google.inject.Inject;
import com.sun.jersey.api.core.InjectParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.NoSuchElementException;

@Path("/services/v1/movies")
public class MovieRS {

    private static final Logger LOG = LoggerFactory.getLogger(MovieRS.class);

    private final MovieService service;

    @Inject
    public MovieRS(MovieService service) {

        this.service = service;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovies(@InjectParam MovieFilter filter) {

        List<Movie> movies = service.findMovies(filter).toBlocking().single();

        return Response.ok(movies).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovie(@PathParam("id") Long id) {

        try {

            Movie movie = service.findMovie(id).toBlocking().single();
            return Response.ok(movie).build();

        } catch (NoSuchElementException e) {
            LOG.error(e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMovie(Movie movie) {

        return Response.status(Response.Status.CREATED).entity(service.createMovie(movie).toBlocking().single()).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateMovie(@PathParam("id") Long id, Movie movie) {

        Movie updatedMovie = service.updateMovie(id, movie).toBlocking().single();
        return Response.ok(updatedMovie).build();
    }

    @DELETE
    @Path("/{id}")
    public Response removeMovie(@PathParam("id") Long id) {

        service.removeMovie(id).toBlocking().single();
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}

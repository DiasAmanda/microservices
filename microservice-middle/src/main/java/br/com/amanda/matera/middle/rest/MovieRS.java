package br.com.amanda.matera.middle.rest;

import br.com.amanda.matera.core.domain.Movie;
import br.com.amanda.matera.middle.service.MovieService;
import com.google.inject.Inject;
import rx.Observable;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Path("/movie")
public class MovieRS {

    private final MovieService service;

    @Inject
    public MovieRS(MovieService service) {

        this.service = service;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") long id) {

        final Movie movie = service.findById(id).toBlocking().singleOrDefault(null);
        if (movie == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(movie).build();
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response all(@QueryParam("name") String title) {

        Observable<Movie> observable;
        if (isNotBlank(title)) {
            observable = service.findByTitle(title);
        } else {
            observable = service.findAll();
        }

        final List<Movie> movies = observable.toList().toBlocking().single();

        if (movies.isEmpty()) {
            Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(movies).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Movie movie) {

        Observable<Movie> insert = service.insert(movie);
        return Response.ok(insert.toBlocking().single()).build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") Long id, Movie movie) {

        Observable<Movie> update = service.update(id, movie);
        return Response.ok(update.toBlocking().single()).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {

        service.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}

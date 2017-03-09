package br.com.amanda.matera.client.api.rest;

import br.com.amanda.matera.client.api.MovieClient;
import br.com.amanda.matera.client.api.command.movie.*;
import br.com.amanda.matera.core.config.Microservice;
import br.com.amanda.matera.core.domain.Movie;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.netflix.niws.client.http.RestClient;
import rx.Observable;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class RestMovieClient implements MovieClient {

    private RestClient restClient;
    private ObjectMapper mapper;

    @Inject
    public RestMovieClient(@Named("ValidationRestClient") RestClient restClient, @Microservice ObjectMapper mapper) {

        this.restClient = restClient;
        this.mapper = mapper;
    }

    @Override
    public Observable<List<Movie>> findAll(String title) {

        return new FindAllMoviesCmd(restClient, mapper, title).observe();
    }

    @Override
    public Observable<Movie> findById(Long id) {

        checkNotNull(id, "id cant be null");
        return new FindMovieByIdCmd(restClient, mapper, id).observe();
    }

    @Override
    public Observable<Movie> create(Movie movie) {

        checkNotNull(movie, "Movie cant be null");
        return new CreateMovieCmd(restClient, mapper, movie).observe();
    }

    @Override
    public Observable<Movie> update(Long id, Movie movie) {

        checkNotNull(id, "id cant be null");
        checkNotNull(movie, "movie cant be null");
        return new UpdateMovieCmd(restClient, mapper, id, movie).observe();
    }

    @Override
    public Observable<Void> delete(Long id) {

        checkNotNull(id, "id cant be null");
        return new DeleteMovieCmd(restClient, id).observe();
    }

}

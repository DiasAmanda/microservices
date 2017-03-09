package br.com.amanda.matera.edge.service;

import br.com.amanda.matera.client.api.MovieClient;
import br.com.amanda.matera.core.domain.Movie;
import br.com.amanda.matera.edge.filter.MovieFilter;
import com.google.inject.Inject;
import rx.Observable;

import java.util.List;

public class MovieService {

    private final MovieClient movieClient;

    @Inject
    public MovieService(MovieClient movieClient) {
        this.movieClient = movieClient;
    }

    public Observable<List<Movie>> findMovies(MovieFilter filter) {

        return movieClient.findAll(filter.getTitle());
    }

    public Observable<Movie> findMovie(Long id) {

        return movieClient.findById(id);
    }

    public Observable<Movie> createMovie(Movie movie) {

        return movieClient.create(movie);
    }

    public Observable<Movie> updateMovie(Long id, Movie movie) {

        return movieClient.update(id, movie);
    }

    public Observable<Void> removeMovie(Long id) {

        return movieClient.delete(id);
    }

}

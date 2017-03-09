package br.com.amanda.matera.client.api;

import br.com.amanda.matera.core.domain.Movie;
import rx.Observable;

import java.util.List;

public interface MovieClient {

    Observable<List<Movie>> findAll(String title);

    Observable<Movie> findById(Long id);

    Observable<Movie> create(Movie movie);

    Observable<Movie> update(Long id, Movie movie);

    Observable<Void> delete(Long id);
}

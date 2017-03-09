package br.com.amanda.matera.middle.dao;

import br.com.amanda.matera.core.domain.Movie;
import rx.Observable;

public interface MovieDAO {

    Observable<Movie> findAll();

    Observable<Movie> findById(long id);

    Observable<Movie> findByTitle(String title);

    void save(Movie person);

    void update(Movie person);

    void delete(Movie person);

    Long getMaxId();
}

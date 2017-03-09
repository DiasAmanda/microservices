package br.com.amanda.matera.middle.service;

import br.com.amanda.matera.core.domain.Movie;
import br.com.amanda.matera.middle.dao.MovieDAO;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import static com.google.common.base.Preconditions.checkArgument;

public class MovieService {

    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    private final MovieDAO movieDAO;

    @Inject
    public MovieService(MovieDAO movieDAO) {

        this.movieDAO = movieDAO;
    }

    public Observable<Movie> findAll() {

        return movieDAO.findAll();
    }

    public Observable<Movie> findById(long id) {

        return movieDAO.findById(id);
    }

    public Observable<Movie> findByTitle(String title) {

        checkArgument(StringUtils.isNotBlank(title), "title cant be null or empty");

        return movieDAO.findByTitle(title);

    }

    public Observable<Movie> insert(Movie movie) {

        checkArgument(!Strings.isNullOrEmpty(movie.getTitle()), "title cant be empty");
        movie.setId(movieDAO.getMaxId() + 1);
        checkArgument(movieDAO.findById(movie.getId()).isEmpty().toBlocking().single(), "id must be unique");

        movieDAO.save(movie);
        return this.findById(movie.getId());
    }

    public Observable<Movie> update(Long id, Movie movie) {

        checkArgument(!movieDAO.findById(id).isEmpty().toBlocking().single(),
                String.format("person not found for %d", id));
        checkArgument(movie.getId() != null, "id cant be empty");
        checkArgument(!Strings.isNullOrEmpty(movie.getTitle()), "title cant be empty");

        movie.setId(id);
        movieDAO.update(movie);

        return this.findById(movie.getId());
    }

    public void delete(Long id) {

        movieDAO.delete(movieDAO.findById(id).toBlocking().single());
    }

}

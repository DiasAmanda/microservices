package br.com.amanda.matera.middle.dao.impl;

import br.com.amanda.matera.core.domain.Movie;
import br.com.amanda.matera.middle.dao.MovieDAO;
import br.com.amanda.matera.middle.query.SearchMovie;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.stream.Collectors;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;

public class MovieDAOCassandra implements MovieDAO {

    private static final Logger logger = LoggerFactory.getLogger(MovieDAOCassandra.class);

    private static final DynamicStringProperty KEYSPACE = DynamicPropertyFactory.getInstance().getStringProperty(
            "microservice-middle.cassandra.keyspace", "microservice");

    private static final DynamicStringProperty CONSISTENCY_LEVEL = DynamicPropertyFactory.getInstance()
            .getStringProperty("microservice-middle.cassandra.concistencylevel", "ONE");

    private static final DynamicStringProperty MOVIE_COLUMN_FAMILY = DynamicPropertyFactory.getInstance()
            .getStringProperty("microservice-middle.cassandra.cf.movie", "movie");

    private static final DynamicStringProperty MOVIE_BY_NAME_COLUMN_FAMILY = DynamicPropertyFactory.getInstance()
            .getStringProperty("microservice-middle.cassandra.cf.moviebyname", "movie_by_name");

    private static final String ID = "id";
    private static final String TITLE = "title";

    private final Provider<Session> session;

    @Inject
    public MovieDAOCassandra(Provider<Session> session) {

        this.session = session;
    }

    @Override
    public Observable<Movie> findAll() {

        final Select query = QueryBuilder.select().from(KEYSPACE.get(), MOVIE_COLUMN_FAMILY.get());

        return new SearchMovie(query, session.get()).observe().flatMap((movies) -> Observable.from(movies));
    }

    @Override
    public Observable<Movie> findById(long id) {

        final Select query = QueryBuilder.select().from(KEYSPACE.get(), MOVIE_COLUMN_FAMILY.get());
        query.where(eq(ID, id));

        return new SearchMovie(query, session.get()).observe().flatMap((movies) -> Observable.from(movies));
    }

    @Override
    public Observable<Movie> findByTitle(String title) {

        final Select query = QueryBuilder.select().from(KEYSPACE.get(), MOVIE_BY_NAME_COLUMN_FAMILY.get());
        query.where(eq(TITLE, title));
        query.allowFiltering();

        return new SearchMovie(query, session.get()).observe().flatMap((movies) -> Observable.from(movies));
    }

    @Override
    public void save(Movie movie) {

        final Insert insertPerson =
                QueryBuilder.insertInto(KEYSPACE.get(), MOVIE_COLUMN_FAMILY.get()).value(ID, movie.getId())
                        .value(TITLE, movie.getTitle());

        session.get().execute(insertPerson.setConsistencyLevel(ConsistencyLevel.valueOf(CONSISTENCY_LEVEL.get())));

        final Insert insertPersonByName =
                QueryBuilder.insertInto(KEYSPACE.get(), MOVIE_BY_NAME_COLUMN_FAMILY.get()).value(ID, movie.getId())
                        .value(TITLE, movie.getTitle());

        session.get()
                .execute(insertPersonByName.setConsistencyLevel(ConsistencyLevel.valueOf(CONSISTENCY_LEVEL.get())));
    }

    @Override
    public void update(Movie movie) {
        delete(movie);
        save(movie);
    }

    @Override
    public void delete(Movie movie) {

        final Delete delete = QueryBuilder.delete().from(KEYSPACE.get(), MOVIE_COLUMN_FAMILY.get());
        delete.where(eq(ID, movie.getId()));

        session.get().execute(delete.setConsistencyLevel(ConsistencyLevel.valueOf(CONSISTENCY_LEVEL.get())));

        final Delete deleteByName = QueryBuilder.delete().from(KEYSPACE.get(), MOVIE_BY_NAME_COLUMN_FAMILY.get());
        deleteByName.where(eq(ID, movie.getId())).and(eq(TITLE, movie.getTitle()));

        session.get().execute(deleteByName.setConsistencyLevel(ConsistencyLevel.valueOf(CONSISTENCY_LEVEL.get())));
    }

    @Override
    public Long getMaxId() {

        final Select query = QueryBuilder.select().from(KEYSPACE.get(), MOVIE_BY_NAME_COLUMN_FAMILY.get());

        return new SearchMovie(query, session.get())
                .observe()
                .map(
                        persons -> {

                            if (persons == null || persons.size() == 0) {
                                return 0L;
                            }

                            Movie maxId =
                                    persons.stream().sorted((a, b) -> (-1) * a.getId().compareTo(b.getId()))
                                            .collect(Collectors.toList()).get(0);

                            return maxId.getId();
                        }).toBlocking().single();
    }

}

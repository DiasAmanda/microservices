package br.com.amanda.matera.middle.query;

import br.com.amanda.matera.core.domain.Movie;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Select;
import com.google.common.collect.Sets;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;

import java.util.Set;

public class SearchMovie extends HystrixCommand<Set<Movie>> {

    private static final HystrixCommand.Setter SETTER = Setter.withGroupKey(
            HystrixCommandGroupKey.Factory.asKey("SearchMovie")).andCommandKey(
            HystrixCommandKey.Factory.asKey(SearchMovie.class.getName()));

    private static final String ID = "id";
    private static final String TITLE = "title";

    private final Select query;
    private final Session session;

    public SearchMovie(Select query, Session session) {

        super(SETTER);

        this.query = query;
        this.session = session;
    }

    @Override
    protected Set<Movie> run() throws Exception {

        final ResultSet resultset = session.execute(query);
        final Set<Movie> result = Sets.newHashSet();
        resultset.forEach((row) -> result.add(toEntity(row)));
        return result;
    }

    private Movie toEntity(Row row) {
        return new Movie(row.getLong(ID), row.getString(TITLE));
    }

}

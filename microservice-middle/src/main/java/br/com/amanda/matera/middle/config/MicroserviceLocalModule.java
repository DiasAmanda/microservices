package br.com.amanda.matera.middle.config;

import br.com.amanda.matera.middle.dao.MovieDAO;
import br.com.amanda.matera.middle.dao.impl.MovieDAOCassandra;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.netflix.governator.guice.lazy.LazySingletonScope;

public class MicroserviceLocalModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MovieDAO.class).to(MovieDAOCassandra.class).in(LazySingletonScope.get());
    }

    @Provides
    public Session cassandraSession() {
        return Cluster.builder().addContactPoint("127.0.0.1").build().connect("microservice");
    }

}

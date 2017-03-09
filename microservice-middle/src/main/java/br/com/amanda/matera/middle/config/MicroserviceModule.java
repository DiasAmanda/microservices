package br.com.amanda.matera.middle.config;

import br.com.amanda.matera.middle.dao.MovieDAO;
import br.com.amanda.matera.middle.dao.impl.MovieDAOCassandra;
import br.com.amanda.matera.middle.service.MovieService;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.evcache.EVCache;
import com.netflix.governator.guice.lazy.LazySingletonScope;

public class MicroserviceModule extends AbstractModule {

    private final DynamicStringProperty cassandraHost =
            DynamicPropertyFactory.getInstance()
                    .getStringProperty("crudmicroservicesmiddle.cassandra.host", "");

    private final DynamicStringProperty cassandraKeyspace =
            DynamicPropertyFactory.getInstance().getStringProperty("crudmicroservicesmiddle.cassandra.keyspace", "");

    private final DynamicStringProperty cacheAppName =
            DynamicPropertyFactory.getInstance().getStringProperty("crudmicroservicesmiddle.evcache.appname", "");

    private final DynamicStringProperty cachePrefix =
            DynamicPropertyFactory.getInstance().getStringProperty("crudmicroservicesmiddle.evcache.prefix", "");

    @Override
    protected void configure() {
        bind(MovieDAO.class).to(MovieDAOCassandra.class).in(LazySingletonScope.get());
        bind(MovieService.class).to(MovieService.class).in(LazySingletonScope.get());
    }

    @Provides
    public Session cassandraSession() {
        return Cluster.builder().addContactPoint(cassandraHost.get()).build().connect(cassandraKeyspace.get());
    }

    @Provides
    public EVCache cache() {
        final EVCache cache =
                (new EVCache.Builder()).setAppName(cacheAppName.get()).setCacheName(cachePrefix.get()).enableZoneFallback().build();
        return cache;
    }
}

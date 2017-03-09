package br.com.amanda.matera.client.config;

import br.com.amanda.matera.client.api.MovieClient;
import br.com.amanda.matera.client.api.rest.RestMovieClient;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.netflix.client.ClientFactory;
import com.netflix.niws.client.http.RestClient;

public class LocalRestClientModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(MovieClient.class).to(RestMovieClient.class).in(Scopes.SINGLETON);
    }

    @Provides
    @Singleton
    @Named("ValidationRestClient")
    public RestClient getRestClient() {

        return (RestClient) ClientFactory.getNamedClient("microservice-middle");
    }
}

package br.com.amanda.matera.edge.config;

import br.com.amanda.matera.client.config.LocalRestClientModule;
import br.com.amanda.matera.core.config.MicroserviceModule;
import com.google.inject.Binder;
import com.netflix.karyon.server.ServerBootstrap;

public class Bootstrap extends ServerBootstrap {

    @Override
    protected void configureBinder(Binder binder) {
        binder.install(new RestModule());
        binder.install(new MicroserviceModule());
        binder.install(new LocalRestClientModule());
    }
}

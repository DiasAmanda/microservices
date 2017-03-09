package br.com.amanda.matera.edge.config;

import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class RestModule extends JerseyServletModule {

    @Override
    protected void configureServlets() {

        bind(GuiceContainer.class);

        final ResourceConfig rc = new PackagesResourceConfig("br.com.amanda.matera.edge.rest");
        for (Class<?> resource : rc.getClasses()) {
            bind(resource);
        }

        serve("/*").with(GuiceContainer.class);
    }
}
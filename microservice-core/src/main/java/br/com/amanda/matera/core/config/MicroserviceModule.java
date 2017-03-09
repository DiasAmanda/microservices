package br.com.amanda.matera.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class MicroserviceModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(ObjectMapper.class).annotatedWith(Microservice.class).toProvider(
                MicroserviceObjectMapperProvider.class);
    }

    @Provides
    @Singleton
    public JacksonJsonProvider getJacksonJsonProvider(@Microservice final ObjectMapper mapper) {

        return new JacksonJsonProvider(mapper, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS);
    }

}

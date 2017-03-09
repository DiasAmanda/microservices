package br.com.amanda.matera.client.api.command.movie;

import br.com.amanda.matera.client.config.MicroserviceGroupKeys;
import br.com.amanda.matera.core.domain.Movie;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.client.http.HttpRequest;
import com.netflix.client.http.HttpResponse;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.niws.client.http.RestClient;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class UpdateMovieCmd extends HystrixCommand<Movie> {

    private static final HystrixCommand.Setter SETTER = Setter.withGroupKey(
            MicroserviceGroupKeys.MIDDLE).andCommandKey(
            HystrixCommandKey.Factory.asKey(UpdateMovieCmd.class.getSimpleName()));

    public static final String DEFAULT_UPDATE_URL = "/microservice-middle/movie/{id}";
    public static final String UPDATE_URL = "microservice.movie.update.url";

    private final RestClient restClient;
    private final ObjectMapper mapper;
    private final Long id;
    private final Movie person;

    public UpdateMovieCmd(final RestClient restClient, final ObjectMapper mapper, Long id, Movie person) {

        super(SETTER);

        this.restClient = restClient;
        this.mapper = mapper;
        this.id = id;
        this.person = person;
    }

    @Override
    protected Movie run() throws Exception {

        String updateURL =
                DynamicPropertyFactory.getInstance().getStringProperty(UPDATE_URL, DEFAULT_UPDATE_URL).get();

        URI URI = UriBuilder.fromPath(updateURL).build(id);

        HttpRequest request =
                HttpRequest.newBuilder().verb(HttpRequest.Verb.PUT).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON).uri(URI).entity(person).build();

        try (HttpResponse response = restClient.executeWithLoadBalancer(request)) {
            return mapper.readValue(response.getInputStream(), Movie.class);
        }
    }
}

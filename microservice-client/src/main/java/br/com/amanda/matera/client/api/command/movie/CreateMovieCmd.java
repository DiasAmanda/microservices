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

public class CreateMovieCmd extends HystrixCommand<Movie> {

    private static final HystrixCommand.Setter SETTER = Setter.withGroupKey(MicroserviceGroupKeys.MIDDLE)
            .andCommandKey(HystrixCommandKey.Factory.asKey(CreateMovieCmd.class.getSimpleName()));

    public static final String DEFAULT_URL = "/microservice-middle/movie";
    public static final String URL = "microservice.movie.create.url";

    private final RestClient restClient;
    private final ObjectMapper mapper;
    private final Movie movie;

    public CreateMovieCmd(final RestClient restClient, final ObjectMapper mapper, final Movie movie) {

        super(SETTER);

        this.restClient = restClient;
        this.mapper = mapper;
        this.movie = movie;
    }

    @Override
    protected Movie run() throws Exception {

        String createURL = DynamicPropertyFactory.getInstance().getStringProperty(URL, DEFAULT_URL).get();

        URI URI = UriBuilder.fromPath(createURL).build();

        HttpRequest request =
                HttpRequest.newBuilder().verb(HttpRequest.Verb.POST).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON).uri(URI).entity(movie).build();

        try (HttpResponse response = restClient.executeWithLoadBalancer(request)) {
            return mapper.readValue(response.getInputStream(), Movie.class);
        }
    }

}

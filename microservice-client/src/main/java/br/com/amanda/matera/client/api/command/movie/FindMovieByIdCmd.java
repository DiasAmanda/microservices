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

public class FindMovieByIdCmd extends HystrixCommand<Movie> {

    private static final HystrixCommand.Setter SETTER = Setter.withGroupKey(MicroserviceGroupKeys.MIDDLE)
            .andCommandKey(HystrixCommandKey.Factory.asKey(FindMovieByIdCmd.class.getSimpleName()));

    public static final String DEFAULT_URL = "/microservice-middle/movie/{id}";
    public static final String URL = "microservice.movie.id.url";

    private final RestClient restClient;
    private final ObjectMapper mapper;
    private final Long id;

    public FindMovieByIdCmd(final RestClient restClient, final ObjectMapper mapper, final Long id) {

        super(SETTER);
        this.restClient = restClient;
        this.mapper = mapper;
        this.id = id;
    }

    @Override
    protected Movie run() throws Exception {

        String findPersonByIdURL = DynamicPropertyFactory.getInstance().getStringProperty(URL, DEFAULT_URL).get();

        URI URI = UriBuilder.fromPath(findPersonByIdURL).build(id);

        HttpRequest request =
                HttpRequest.newBuilder().verb(HttpRequest.Verb.GET).header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON).uri(URI)
                        .build();

        try (HttpResponse response = restClient.executeWithLoadBalancer(request)) {
            return mapper.readValue(response.getInputStream(), Movie.class);
        }
    }

}

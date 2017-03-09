package br.com.amanda.matera.client.api.command.movie;

import br.com.amanda.matera.client.config.MicroserviceGroupKeys;
import br.com.amanda.matera.core.domain.Movie;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.client.http.HttpRequest;
import com.netflix.client.http.HttpRequest.Verb;
import com.netflix.client.http.HttpResponse;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.niws.client.http.RestClient;

import java.util.List;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class FindAllMoviesCmd extends HystrixCommand<List<Movie>> {

    private static final HystrixCommand.Setter SETTER = Setter.withGroupKey(MicroserviceGroupKeys.MIDDLE)
            .andCommandKey(HystrixCommandKey.Factory.asKey(FindAllMoviesCmd.class.getSimpleName()));

    public static final String DEFAULT_URL = "/microservice-middle/movie/";
    public static final String URL = "microservice.movie.all.url";

    private final RestClient restClient;
    private final ObjectMapper mapper;

    private final String title;

    public FindAllMoviesCmd(RestClient restClient, ObjectMapper mapper, String title) {

        super(SETTER);

        this.restClient = restClient;
        this.mapper = mapper;
        this.title = title;
    }

    @Override
    protected List<Movie> run() throws Exception {

        String findAllUrl = DynamicPropertyFactory.getInstance().getStringProperty(URL, DEFAULT_URL).get();
        UriBuilder builder = UriBuilder.fromPath(findAllUrl);

        if (isNotBlank(title)) {
            builder.queryParam("title", title);
        }

        HttpRequest request =
                HttpRequest.newBuilder().verb(Verb.GET).header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                        .uri(builder.build()).build();

        try (HttpResponse response = restClient.executeWithLoadBalancer(request)) {
            return mapper.readValue(response.getInputStream(), new TypeReference<List<Movie>>() {
            });
        }
    }

}

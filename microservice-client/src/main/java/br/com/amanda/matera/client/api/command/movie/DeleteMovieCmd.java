package br.com.amanda.matera.client.api.command.movie;

import br.com.amanda.matera.client.config.MicroserviceGroupKeys;
import com.netflix.client.ClientException;
import com.netflix.client.http.HttpRequest;
import com.netflix.client.http.HttpResponse;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.niws.client.http.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.UriBuilder;

import static com.google.common.base.Preconditions.checkArgument;

public class DeleteMovieCmd extends HystrixCommand<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteMovieCmd.class);

    private static final HystrixCommand.Setter SETTER = Setter.withGroupKey(MicroserviceGroupKeys.MIDDLE)
            .andCommandKey(HystrixCommandKey.Factory.asKey(DeleteMovieCmd.class.getSimpleName()));

    private static final String DEFAULT_DELETE_URL = "/microservice-middle/movie/{id}";
    private static final String DELETE_URL = "microservice.movie.delete.url";

    private final RestClient restClient;
    private final Long id;

    public DeleteMovieCmd(final RestClient restClient, final Long id) {

        super(SETTER);

        this.restClient = restClient;
        this.id = id;
    }

    @Override
    protected Void run() throws Exception {

        final String url =
                DynamicPropertyFactory.getInstance().getStringProperty(DELETE_URL, DEFAULT_DELETE_URL).get();

        HttpRequest request =
                HttpRequest.newBuilder().verb(HttpRequest.Verb.DELETE).uri(UriBuilder.fromPath(url).build(id)).build();

        try (HttpResponse response = restClient.executeWithLoadBalancer(request)) {
            checkArgument(response.isSuccess(), "Error deleting movie");
        } catch (ClientException e) {
            LOGGER.error("Error deleting movie", e);
            throw e;
        }

        return null;
    }
}

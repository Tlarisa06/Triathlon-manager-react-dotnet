package ro.mpp.triathlon.rest.client;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import java.io.IOException;

public class LoggingInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        System.out.println(">>> Sending " + request.getMethod() + " request to: " + request.getURI());

        ClientHttpResponse response = execution.execute(request, body);

        System.out.println("<<< Received response status: " + response.getStatusCode());
        return response;
    }
}
package org.sds.sonizone.order.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequestInterceptor;

import java.io.IOException;

@Configuration
@Component
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LogManager.getLogger(AppConfig.class);

    private OAuth2AuthorizedClientManager manager;

    public RestTemplateInterceptor(OAuth2AuthorizedClientManager manager){
        this.manager = manager;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        String oktaToken = manager.authorize(OAuth2AuthorizeRequest.withClientRegistrationId("my-internal-client").
                principal("internal").build()).getAccessToken().getTokenValue();

        logger.info("In RestTemplateInterceptor: Token value is: {}", oktaToken);

        request.getHeaders().add("Authorization", "Bearer " + oktaToken);
        return execution.execute(request, body);
    }
}

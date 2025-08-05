package org.sds.sonizone.order.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sds.sonizone.order.adapters.in.rest.OrderController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AppConfig {
    private static final Logger logger = LogManager.getLogger(AppConfig.class);
    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;
    @Autowired
    OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;
    @Bean
    public RestTemplate getRestTemplate() {
        //return new RestTemplate();

        RestTemplate restTemplate = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptor = new ArrayList<>();

        interceptor.add(new RestTemplateInterceptor(manager(
                clientRegistrationRepository, oAuth2AuthorizedClientRepository
        )));
        restTemplate.setInterceptors(interceptor);
        return restTemplate;
    }
    //Declare the bean of OAuth2AuthorizedClientManager manager
    @Bean
    public OAuth2AuthorizedClientManager manager(ClientRegistrationRepository clientRegistrationRepository,
                                                 OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository) {
        OAuth2AuthorizedClientProvider provider = OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build();
        DefaultOAuth2AuthorizedClientManager defaultOAuth2AuthorizedClientManager =
                new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, oAuth2AuthorizedClientRepository);
        defaultOAuth2AuthorizedClientManager.setAuthorizedClientProvider(provider);
        return defaultOAuth2AuthorizedClientManager;
    }
}


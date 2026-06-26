package com.example.demo.config;

import java.net.http.HttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(ExternalApiProperties.class)
public class RestClientConfig {

    @Bean
    public RestClient externalTasksRestClient(RestClient.Builder restClientBuilder,
            ExternalApiProperties externalApiProperties) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(externalApiProperties.getConnectTimeout())
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(externalApiProperties.getReadTimeout());

        return restClientBuilder
                .requestFactory(requestFactory)
                .defaultHeader(HttpHeaders.USER_AGENT, externalApiProperties.getUserAgent())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}

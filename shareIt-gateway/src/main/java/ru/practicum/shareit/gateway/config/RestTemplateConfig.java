package ru.practicum.shareit.gateway.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient()));
    }

    @Bean
    public CloseableHttpClient httpClient() {
        return HttpClients.custom()
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .build();
    }

    @Bean
    public RestTemplateCustomizer restTemplateCustomizer() {
        return restTemplate -> restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient()));
    }
}
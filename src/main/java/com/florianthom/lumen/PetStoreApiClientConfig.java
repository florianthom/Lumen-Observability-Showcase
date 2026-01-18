package com.florianthom.lumen;

import com.florianthom.petstore.javaClient.api.PetApi;
import com.florianthom.petstore.javaClient.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PetStoreApiClientConfig {

    @Bean
    public PetApi petApi(ApiClient apiClient) {
        return new PetApi();
    }

    @Bean
    public ApiClient petStoreApiClient(
           @Value("${lumen.petstore.url}") String basePath,
           @Value("${lumen.petstore.api-key}") String apiKey,
           WebClient.Builder webClientBuilder
   ) {
        var webClient = webClientBuilder
            .exchangeStrategies(ExchangeStrategies.builder().build())
            .build();
        ApiClient apiClient = new ApiClient(webClient);
        apiClient.setBasePath(basePath);
        apiClient.setApiKey(apiKey);
        return apiClient;
   };


}

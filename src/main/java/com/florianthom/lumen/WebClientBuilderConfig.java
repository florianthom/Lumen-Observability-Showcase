package com.florianthom.lumen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientBuilderConfig {
    private Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .filter(loggingFilter());
    }

    private ExchangeFilterFunction loggingFilter() {
        return (request, next) -> {
            long start = System.nanoTime();

            // log request
            logger.atInfo()
                    .addKeyValue("event.type", "http.client")
                    .addKeyValue("event.action", "request")
                    .addKeyValue("http.method", request.method().name())
                    .addKeyValue("url.full", request.url().toString())
                    .log("Outgoing HTTP request");

            // continue with request
            return next.exchange(request)
                    .doOnSuccess(response -> {
                        long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

                        // log response
                        logger.atInfo()
                                .addKeyValue("event.type", "http.client")
                                .addKeyValue("event.action", "response")
                                .addKeyValue("http.method", request.method().name())
                                .addKeyValue("url.full", request.url().toString())
                                .addKeyValue("http.status_code", response.statusCode().value())
                                .addKeyValue("event.duration", durationMs)
                                .log("HTTP call completed");
                    })
                    .doOnError(error -> {
                        long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

                        // log error
                        logger.atError()
                                .addKeyValue("event.type", "http.client")
                                .addKeyValue("event.action", "response")
                                .addKeyValue("http.method", request.method().name())
                                .addKeyValue("url.full", request.url().toString())
                                .addKeyValue("event.duration", durationMs)
                                .addKeyValue("error.type", error.getClass().getSimpleName())
                                .addKeyValue("error.message", error.getMessage())
                                .log("HTTP call failed");
                    });
        };
    }
}
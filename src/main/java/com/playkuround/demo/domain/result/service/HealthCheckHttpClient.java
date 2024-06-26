package com.playkuround.demo.domain.result.service;

import com.playkuround.demo.domain.result.dto.HostAndStatus;
import io.netty.channel.ChannelOption;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;

@Component
public class HealthCheckHttpClient {

    private final WebClient webClient;

    public HealthCheckHttpClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(3000));

        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public List<HostAndStatus> exchangeHttp(List<String> urls) {
        return Flux.fromIterable(urls)
                .flatMap(url ->
                        fetchStatusCode(url)
                                .onErrorResume(this::handleNetworkErrors)
                                .map(status -> new HostAndStatus(url, status))
                )
                .collectList()
                .block();
    }

    private Mono<Integer> fetchStatusCode(String url) {
        return this.webClient.get()
                .uri(url)
                .exchangeToMono(response -> Mono.just(response.statusCode().value()));
    }

    private Mono<Integer> handleNetworkErrors(Throwable throwable) {
        if (throwable instanceof WebClientRequestException) {
            return Mono.just(400);
        }
        return Mono.just(500);
    }

}

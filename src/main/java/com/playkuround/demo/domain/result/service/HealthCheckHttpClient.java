package com.playkuround.demo.domain.result.service;

import com.playkuround.demo.domain.result.dto.TargetAndStatus;
import com.playkuround.demo.domain.target.entity.Target;
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

    public List<TargetAndStatus> exchangeHttp(List<Target> targets) {
        return Flux.fromIterable(targets)
                .flatMap(target ->
                        fetchStatusCode(target.getHealthCheckURL())
                                .onErrorResume(this::handleNetworkErrors)
                                .map(result -> new TargetAndStatus(target, result.status, result.errorLog))
                )
                .collectList()
                .block();
    }

    private Mono<StatusAndErrorLog> fetchStatusCode(String url) {
        return this.webClient.get()
                .uri(url)
                .exchangeToMono(response ->
                        Mono.just(new StatusAndErrorLog(response.statusCode().value(), null))
                );
    }

    private Mono<StatusAndErrorLog> handleNetworkErrors(Throwable throwable) {
        if (throwable instanceof WebClientRequestException) {
            return Mono.just(new StatusAndErrorLog(499, throwable.getMessage()));
        }
        return Mono.just(new StatusAndErrorLog(599, throwable.getMessage()));
    }

    private record StatusAndErrorLog(int status, String errorLog) {
    }
}

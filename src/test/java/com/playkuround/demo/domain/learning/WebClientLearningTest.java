package com.playkuround.demo.domain.learning;

import com.playkuround.demo.domain.result.dto.HostAndStatus;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;

@Slf4j
class WebClientLearningTest {

    private final WebClient webClient;

    private WebClientLearningTest() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(3000));

        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    private List<HostAndStatus> exchangeHttp(List<String> urls) {
        return Flux.fromIterable(urls)
                .flatMap(url ->
                        fetchStatusCode(url)
                                .onErrorResume(this::handleNetworkErrors)
                                .map(status -> new HostAndStatus(url, status))
                                .log(url)
                )
                .collectList()
                .block();
    }

    private Mono<Integer> fetchStatusCode(String url) {
        log.info("fetchStatusCode: url={}", url);
        return this.webClient.get()
                .uri(url)
                .exchangeToMono(response -> Mono.just(response.statusCode().value()));
    }

    private Mono<Integer> handleNetworkErrors(Throwable throwable) {
        log.info("handleNetworkErrors");
        if (throwable instanceof WebClientRequestException) {
            return Mono.just(400);
        }
        return Mono.just(500);
    }

    @Test
    @DisplayName("각 요청은 별도의 쓰레드에서 비동기로 처리되고, 마지막 최종 결과는 blocking으로 처리된다.")
    void exchangeHttp() {
        // given
        List<String> urls = List.of("https://playkuround.com/api/system-available", "https://playkuround.com");

        // when
        List<HostAndStatus> hostAndStatuses = exchangeHttp(urls);

        // then
        hostAndStatuses.forEach(hostAndStatus -> log.info("hostAndStatus={}", hostAndStatus));
    }

}

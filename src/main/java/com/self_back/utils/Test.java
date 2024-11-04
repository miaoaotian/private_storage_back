package com.self_back.utils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

public class Test {
    private WebClient webClient;

    public Test() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.aiproxy.io/v1/chat")
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization", "Bearer **************************")  // 替换成实际的 API 密钥
                .build();
    }

    public Mono<String> sendMessage(String content) {
        String requestBody = String.format("{\"model\":\"gpt-4o-mini\", \"messages\":[{\"role\":\"user\", \"content\":\"%s\"}], \"temperature\":0.7, \"session_id\":\"abcdef\", \"session_limit\":2}", content);

        return this.webClient.post()
                .uri("/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                    // 处理错误状态码
                    return response.bodyToMono(String.class).flatMap(body -> Mono.error(new RuntimeException("API Error: " + body)));
                })
                .bodyToMono(String.class);
    }

    public static void main(String[] args) {
        Test client = new Test();
        client.sendMessage("你能帮我写个线段树代码吗？").subscribe(System.out::println, Throwable::printStackTrace);
    }
}

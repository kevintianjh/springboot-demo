package kevintian.springbootdemo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
public class WebClientController {

    @GetMapping("/test/add")
    public String addFunction(Integer int1, Integer int2) {

        WebClient client = WebClient.create("http://localhost:8081");

        Mono<String> ret = client
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/test/add")
                        .queryParam("int1", int1)
                        .queryParam("int2", int2)
                        .build())
                .retrieve()
                .bodyToMono(String.class);

        return ret.block();
    }

    @GetMapping("/test/concat")
    public String concatFunction(String str1, String str2) throws JsonProcessingException {
        WebClient client = WebClient.create("http://localhost:8081");
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> bodyValues = new HashMap<>();
        bodyValues.put("str1", str1);
        bodyValues.put("str2", str2);

        Mono<String> ret = client
                .post()
                .uri("/test/concat")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(bodyValues))
                .retrieve()
                .bodyToMono(String.class);

        CompletableFuture<String> p1 = CompletableFuture.supplyAsync(ret::block);

        return p1.join();
    }
}
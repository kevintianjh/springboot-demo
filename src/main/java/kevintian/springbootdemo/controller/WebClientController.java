package kevintian.springbootdemo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.print.attribute.standard.Media;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
public class WebClientController {

    private WebClient webClient;
    private String baseUrl = "http://localhost:8081";

    @Autowired
    public WebClientController(WebClient webClient) {
        this.webClient = webClient;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @PostMapping("/test/add")
    public ResponseEntity<String> addFunction(@RequestBody Map<String,Integer> params) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String bodyValue = objectMapper.writeValueAsString(params);

        if(!(params.containsKey("int1") && params.containsKey("int2"))) {
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }

        Mono<String> ret = this.webClient
                .post()
                .uri(this.baseUrl+ "/test/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bodyValue)
                .retrieve()
                .bodyToMono(String.class);

        return new ResponseEntity<>(ret.block(), HttpStatus.OK);
    }

    @GetMapping("/test/concat")
    public ResponseEntity<String> concatFunction(String str1, String str2) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> bodyValues = new HashMap<>();
        bodyValues.put("str1", str1);
        bodyValues.put("str2", str2);

        Mono<String> ret = this.webClient
                .post()
                .uri(this.baseUrl + "/test/concat")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(bodyValues))
                .retrieve()
                .bodyToMono(String.class);

        CompletableFuture<String> retFuture = CompletableFuture.supplyAsync(ret::block);

        return new ResponseEntity<>(retFuture.get(), HttpStatus.OK);
    }
}
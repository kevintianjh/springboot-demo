package kevintian.springbootdemo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import kevintian.springbootdemo.controller.dto.AuthCallbackRsp;
import kevintian.springbootdemo.controller.dto.ValidateTokenRsp;
import kevintian.springbootdemo.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@CrossOrigin
public class TestController {
    private JwtService jwtService;
    @Autowired
    public TestController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/test/auth/sso")
    public ResponseEntity<String> test1() {

        ResponseCookie rc1 = ResponseCookie
                .from("key1", "HelloKevinTian")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofMinutes(30))
                .build();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.SET_COOKIE, rc1.toString());

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body("{\"key1\":\"Hello World!\"}");
    }

    @GetMapping("/test/auth-callback")
    public ResponseEntity<AuthCallbackRsp> authCallback(String token) throws JsonProcessingException {

        //Call API to verify token
        WebClient webClient = WebClient.create("http://localhost:8081");

        ValidateTokenRsp validateTokenRsp = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/test/validate-token")
                        .queryParam("token", token)
                        .build())
                .retrieve()
                .bodyToMono(ValidateTokenRsp.class)
                .block();

        if(!validateTokenRsp.valid) {
            return ResponseEntity.badRequest().build();
        }

        //Decode payload of the token and convert it into Map
        String payload = new String(Base64Utils.decodeFromUrlSafeString(token.split("\\.")[1]));
        ObjectMapper objectMapper = new ObjectMapper();
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(Map.class, String.class, String.class);
        Map<String,String> params = objectMapper.readValue(payload, javaType);

        //Populate response object with attributes
        AuthCallbackRsp authCallbackRsp = new AuthCallbackRsp();
        authCallbackRsp.valid = true;
        authCallbackRsp.userId = params.get("sub");
        authCallbackRsp.roles = params.get("roles");
        authCallbackRsp.token = this.jwtService.generateToken(Integer.parseInt(authCallbackRsp.userId), authCallbackRsp.roles);

        return ResponseEntity.ok(authCallbackRsp);
    }
}

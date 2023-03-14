package kevintian.springbootdemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class WebClientControllerTest {

    private static MockWebServer mockWebServer;

    @Autowired private MockMvc mockMvc;

    @Autowired private WebClientController webClientController;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockWebServer.close();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        this.webClientController.setBaseUrl(baseUrl);
    }

    @Test
    void addFunctionTest() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("45")
                .addHeader("Content-Type", "text/plain"));

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,Integer> params = new HashMap<>();
        params.put("int1", 20);
        params.put("int2", 25);
        String body = objectMapper.writeValueAsString(params);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders
                                .post("http://localhost/test/add")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andReturn();

        //Verify output
        Assertions.assertEquals("45", mvcResult.getResponse().getContentAsString());

        //Verify request sent
        RecordedRequest request = mockWebServer.takeRequest();
        Assertions.assertEquals("POST", request.getMethod());
        Assertions.assertEquals("application/json", request.getHeader("Content-Type"));
        Assertions.assertTrue(request.getBody().toString().contains(body));
        Assertions.assertEquals("/test/add", request.getPath());
    }

    @Test
    void concatFunctionTest() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("kevin tian")
                .addHeader("Content-Type", "text/plain"));

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> bodyValues = new HashMap<>();
        bodyValues.put("str1", "kevin");
        bodyValues.put("str2", "tian");
        String body = objectMapper.writeValueAsString(bodyValues);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders
                                            .get("http://localhost/test/concat")
                                            .queryParam("str1", "kevin")
                                            .queryParam("str2", "tian")
                )
                .andReturn();

        //Verify output
        Assertions.assertEquals("kevin tian", mvcResult.getResponse().getContentAsString());

        //Verify request sent
        RecordedRequest request = mockWebServer.takeRequest();
        Assertions.assertEquals("POST", request.getMethod());
        Assertions.assertEquals("application/json", request.getHeader("Content-Type"));
        Assertions.assertTrue(request.getBody().toString().contains(body));
        Assertions.assertEquals("/test/concat", request.getPath());

        System.out.println(request.getBody().toString());
    }
}
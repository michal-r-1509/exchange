package com.michal.converter.api;

import com.michal.converter.dto.RequestDto;
import com.michal.converter.dto.ResponseDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConverterControllerIntegrationTest {

    private static MockWebServer mockWebServer;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int serverPort;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void returns2xxSuccessfulAndValidValues() {
        double value = 20;
        RequestDto requestDto = new RequestDto();
        requestDto.setValue(BigDecimal.valueOf(value));
        requestDto.setInputCurrency("usd");
        requestDto.setOutputCurrency("pln");

        RequestEntity<RequestDto> request = RequestEntity
                .post(getServerAddress())
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestDto);

        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("{\"rates\": [{\"mid\": 4.5}]}")
        );

        ResponseEntity<ResponseDto> response = restTemplate.exchange(request, ResponseDto.class);
        Double exchangeRate = Double.valueOf(response.getBody().getExchangeRate());
        Double result = Double.valueOf(response.getBody().getResult());

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(4.5, exchangeRate);
        assertEquals(value * 4.5, result);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "usd,pln,0",
            "usd,pln,999999999.99"
    })
    void returns2xxSuccessful(String input, String output, BigDecimal value) {
        RequestDto requestDto = new RequestDto();
        requestDto.setInputCurrency(input);
        requestDto.setOutputCurrency(output);
        requestDto.setValue(value);

        RequestEntity<RequestDto> request = RequestEntity
                .post(getServerAddress())
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestDto);

        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("{\"rates\": [{\"mid\": 0}]}")
        );

        ResponseEntity<ResponseDto> response = restTemplate.exchange(request, ResponseDto.class);

        assertTrue(response.getStatusCode().is2xxSuccessful());
    }


    @ParameterizedTest
    @CsvSource(value = {
            "usd,pln, ",
            "usd,,1",
            ",,1",
            ",,",
            " , , ",
            "123,pln,1",
            "ussd,pl,1",
            "usd,pln,-1",
            "usd,pln,1000000000",
    })
    void returns4xx_whenRequestNotValid(String input, String output, BigDecimal value) {
        RequestDto requestDto = new RequestDto();
        requestDto.setInputCurrency(input);
        requestDto.setOutputCurrency(output);
        requestDto.setValue(value);

        RequestEntity<RequestDto> request = RequestEntity
                .post(getServerAddress())
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestDto);

        ResponseEntity<ResponseDto> response = restTemplate.exchange(request, ResponseDto.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    private URI getServerAddress() {
        try {
            return new URI("http://localhost:" + serverPort + "/api/convert");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry){
        registry.add("webapi.currency-request-url", () -> mockWebServer.url("/").url().toString());
    }
}
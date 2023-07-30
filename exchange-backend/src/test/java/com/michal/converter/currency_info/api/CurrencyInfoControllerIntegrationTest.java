package com.michal.converter.currency_info.api;

import com.michal.converter.currency_info.dto.InfoRequestDto;
import com.michal.converter.currency_info.dto.InfoResponseDto;
import com.michal.converter.currency_info.web_service_dto.FlagDto;
import com.michal.converter.currency_info.web_service_dto.NameDto;
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
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CurrencyInfoControllerIntegrationTest {

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
        InfoRequestDto requestDto = new InfoRequestDto();
        requestDto.setCode("usd");

        RequestEntity<InfoRequestDto> request = RequestEntity
                .post(getServerAddress())
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestDto);

        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(getInfoText())
        );

        ResponseEntity<InfoResponseDto> response = restTemplate.exchange(request, InfoResponseDto.class);
        InfoResponseDto body = response.getBody();
        InfoResponseDto pattern = getResponsePattern();

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(pattern.getCode(), body.getCode());
        assertEquals(pattern.getName().getCommon(), body.getName().getCommon());
        assertEquals(pattern.getName().getOfficial(), body.getName().getOfficial());
        assertEquals(pattern.getRegion(), body.getRegion());
        assertEquals(pattern.getSubregion(), body.getSubregion());
        assertEquals(pattern.getFlags().getSvg(), body.getFlags().getSvg());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "us",
            "ussd",
            "123"
    })
    void returns4xx_whenRequestNotValid(String code) {
        InfoRequestDto requestDto = new InfoRequestDto();
        requestDto.setCode(code);

        RequestEntity<InfoRequestDto> request = RequestEntity
                .post(getServerAddress())
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestDto);

        ResponseEntity<InfoResponseDto> response = restTemplate.exchange(request, InfoResponseDto.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

        private URI getServerAddress() {
        try {
            return new URI("http://localhost:" + serverPort + "/api/info");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry){
        registry.add("webapi.currency-info-url", () -> mockWebServer.url("/").url().toString());
    }

    private InfoResponseDto getResponsePattern(){
        FlagDto flagDto = new FlagDto();
        flagDto.setSvg("https://flagcdn.com/us.svg");
        NameDto nameDto = new NameDto();
        nameDto.setCommon("USA");
        nameDto.setOfficial("United States of America");

        InfoResponseDto responseDto = new InfoResponseDto();
        responseDto.setCode("usd");
        responseDto.setName(nameDto);
        responseDto.setRegion("Hawaii");
        responseDto.setSubregion("Pacific Ocean");
        responseDto.setFlags(flagDto);

        return responseDto;
    }

    private String getInfoText(){
        return "[{" +
                "    \"name\": {" +
                "        \"common\": \"USA\"," +
                "        \"official\": \"United States of America\"" +
                "    },\n" +
                "    \"region\": \"Hawaii\"," +
                "    \"subregion\": \"Pacific Ocean\"," +
                "    \"flags\": {" +
                "        \"svg\": \"https://flagcdn.com/us.svg\"" +
                "    }" +
                "}]";
    }
}
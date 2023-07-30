package com.michal.converter.web_service;

import com.michal.converter.currency_info.dto.CurrencyInfoDto;
import com.michal.converter.exceptions.ApiNotFoundException;
import com.michal.converter.exceptions.DataNotFoundException;
import com.michal.converter.web_service.dto.RateDto;
import com.michal.converter.web_service.dto.Rates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiRequestTest {

    @Mock
    private RestTemplate restTemplate;
    private ApiRequest underTest;
    private final ParameterizedTypeReference<CurrencyInfoDto[]> responseType = new ParameterizedTypeReference<>() {};
    private Field restTemplateField;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        underTest = new ApiRequest();
        restTemplateField = underTest.getClass().getDeclaredField("restTemplate");
        restTemplateField.setAccessible(true);

        String rateUrl = "rateUrl";
        Field rateUrlField = underTest.getClass().getDeclaredField("rateUrl");
        rateUrlField.setAccessible(true);
        rateUrlField.set(underTest, rateUrl);

        String infoUrl = "infoUrl";
        Field infoUrlField = underTest.getClass().getDeclaredField("infoUrl");
        infoUrlField.setAccessible(true);
        infoUrlField.set(underTest, infoUrl);
    }

    @Test
    void exchangeRateApiRequest_throwsApiNotFoundException_whenIssueWithUrl() {
        when(restTemplate.getForObject(anyString(), any(), anyString()))
                .thenThrow(new HttpStatusCodeException(HttpStatus.NOT_FOUND){});
        setRestTemplateField();
        assertThrows(ApiNotFoundException.class, () -> underTest.exchangeRateApiRequest("pln"));
    }

    @Test
    void exchangeRateApiRequest_throwsDataNotFoundException_whenRateIsEmpty() {
        RateDto rateDto = new RateDto();
        rateDto.setRates(List.of());
        when(restTemplate.getForObject(anyString(), any(), anyString()))
                .thenReturn(rateDto);
        setRestTemplateField();
        assertThrows(DataNotFoundException.class, () -> underTest.exchangeRateApiRequest("pln"));
    }

    @Test
    void exchangeRateApiRequest_throwsDataNotFoundException_whenRateDtoIsNull() {
        when(restTemplate.getForObject(anyString(), any(), anyString()))
                .thenReturn(null);
        setRestTemplateField();
        assertThrows(DataNotFoundException.class, () -> underTest.exchangeRateApiRequest("pln"));
    }

    @Test
    void exchangeRateApiRequest_throwsDataNotFoundException_whenRateNotEmptyAndValueIsNull() {
        RateDto rateDto = new RateDto();
        Rates rates = new Rates();
        rates.setMid(null);
        rateDto.setRates(List.of(rates));
        when(restTemplate.getForObject(anyString(), any(), anyString()))
                .thenReturn(rateDto);
        setRestTemplateField();
        assertThrows(DataNotFoundException.class, () -> underTest.exchangeRateApiRequest("pln"));
    }

    @Test
    void exchangeRateApiRequest_returnsBigDecimalOne_whenRateValueExists() {
        RateDto rateDto = new RateDto();
        Rates rates = new Rates();
        rates.setMid(BigDecimal.ONE);
        rateDto.setRates(List.of(rates));
        when(restTemplate.getForObject(anyString(), any(), anyString()))
                .thenReturn(rateDto);
        setRestTemplateField();
        BigDecimal result = underTest.exchangeRateApiRequest("pln");
        assertThat(BigDecimal.ONE.compareTo(result)).isEqualTo(0);
    }

    @Test
    void currencyInfoApiRequest_throwsApiNotFoundException_whenIssueWithUrl() {
        when(restTemplate.exchange(anyString(), any(), any(), eq(responseType), anyString()))
                .thenThrow(new HttpStatusCodeException(HttpStatus.NOT_FOUND){});
        setRestTemplateField();
        assertThrows(ApiNotFoundException.class, () -> underTest.currencyInfoApiRequest("pln"));
    }

    @Test
    void currencyInfoApiRequest_throwsDataNotFoundException_whenResponseIsEmpty() {
        CurrencyInfoDto[] currencyInfoDto = {};
        ResponseEntity<CurrencyInfoDto[]> infoResponse = ResponseEntity.ok(currencyInfoDto);
        when(restTemplate.exchange(anyString(), any(), any(), eq(responseType), anyString()))
                .thenReturn(infoResponse);
        setRestTemplateField();
        assertThrows(DataNotFoundException.class, () -> underTest.currencyInfoApiRequest("pln"));
    }

    @Test
    void currencyInfoApiRequest_throwsDataNotFoundException_whenResponseBodyIsNull() {
        ResponseEntity<CurrencyInfoDto[]> infoResponse = ResponseEntity.ok(null);
        when(restTemplate.exchange(anyString(), any(), any(), eq(responseType), anyString()))
                .thenReturn(infoResponse);
        setRestTemplateField();
        assertThrows(DataNotFoundException.class, () -> underTest.currencyInfoApiRequest("pln"));
    }

    @Test
    void currencyInfoApiRequest_throwsDataNotFoundException_whenHttpStatusCodeIsNot2xx() {
        CurrencyInfoDto[] currencyInfoDto = {};
        ResponseEntity<CurrencyInfoDto[]> infoResponse =
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(currencyInfoDto);
        when(restTemplate.exchange(anyString(), any(), any(), eq(responseType), anyString()))
                .thenReturn(infoResponse);
        setRestTemplateField();
        assertThrows(DataNotFoundException.class, () -> underTest.currencyInfoApiRequest("pln"));
    }

    @Test
    void currencyInfoApiRequest_returnsNotEmptyList() {
        CurrencyInfoDto[] currencyInfoDto = {new CurrencyInfoDto()};
        ResponseEntity<CurrencyInfoDto[]> infoResponse = ResponseEntity.ok(currencyInfoDto);
        when(restTemplate.exchange(anyString(), any(), any(), eq(responseType), anyString()))
                .thenReturn(infoResponse);
        setRestTemplateField();
        List<CurrencyInfoDto> currencyInfoDtoList = underTest.currencyInfoApiRequest("pln");
        assertThat(currencyInfoDtoList).isNotEmpty();
    }

    private void setRestTemplateField() {
        try {
            restTemplateField.set(underTest, restTemplate);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
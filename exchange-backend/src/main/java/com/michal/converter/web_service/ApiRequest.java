package com.michal.converter.web_service;

import com.michal.converter.exceptions.DataNotFoundException;
import com.michal.converter.web_service.dto.RateDto;
import com.michal.converter.web_service.dto.Rates;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApiRequest {
    @Value("${webapi.request-url}")
    private String url;

    private final RestTemplate restTemplate;

    public BigDecimal apiRequest(String currency) {
        RateDto rateDto;
        Rates rate = null;
        try {
            rateDto = restTemplate.getForObject(url + "{code}/", RateDto.class, currency);
        } catch (HttpStatusCodeException exception) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }

        if (rateDto != null) {
            rate = Optional.ofNullable(rateDto.getRates().get(0))
                    .orElseThrow(() -> new DataNotFoundException("Exchange rate not found"));
        }
        return rate.getMid();
    }
}

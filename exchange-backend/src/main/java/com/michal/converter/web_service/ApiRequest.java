package com.michal.converter.web_service;

import com.michal.converter.currency_info.dto.CurrencyInfoDto;
import com.michal.converter.exceptions.DataNotFoundException;
import com.michal.converter.web_service.dto.RateDto;
import com.michal.converter.web_service.dto.Rates;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApiRequest {
    @Value("${webapi.currency-request-url}")
    private String rateUrl;

    @Value("${webapi.currency-info-url}")
    private String infoUrl;

    private final RestTemplate restTemplate;

    public BigDecimal apiRequest(String currency) {
        RateDto rateDto;
        Rates rate = null;
        try {
            rateDto = restTemplate.getForObject(rateUrl + "{code}/", RateDto.class, currency);
        } catch (HttpStatusCodeException exception) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }

        if (rateDto != null) {
            rate = Optional.ofNullable(rateDto.getRates().get(0))
                    .orElseThrow(() -> new DataNotFoundException("Exchange rate not found"));
        }
        return rate.getMid();
    }

    public List<CurrencyInfoDto> currencyInfoApiRequest(String code){
        ResponseEntity<CurrencyInfoDto[]> infoTable;
        List<CurrencyInfoDto> infoList = new ArrayList<>();
        try {
            infoTable = restTemplate.getForEntity(infoUrl + "{code}/", CurrencyInfoDto[].class, code);
        } catch (HttpStatusCodeException exception) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }

        if (infoTable.hasBody()) {
            infoList = Arrays.asList(Optional.ofNullable(infoTable.getBody())
                    .orElseThrow(() -> new DataNotFoundException("Currency info not found")));
        }
        return infoList;
    }
}

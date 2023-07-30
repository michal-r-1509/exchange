package com.michal.converter.web_service;

import com.michal.converter.currency_info.dto.CurrencyInfoDto;
import com.michal.converter.exceptions.ApiNotFoundException;
import com.michal.converter.exceptions.DataNotFoundException;
import com.michal.converter.web_service.dto.RateDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class ApiRequest {
    @Value("${webapi.currency-request-url}")
    private String rateUrl;

    @Value("${webapi.currency-info-url}")
    private String infoUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public BigDecimal exchangeRateApiRequest(String currency) {
        RateDto rateDto;
        BigDecimal exchangeRate;
        try {
            rateDto = restTemplate.getForObject(rateUrl + "{code}/", RateDto.class, currency);
        } catch (HttpStatusCodeException exception) {
            throw new ApiNotFoundException("Exchange rate API not found");
        }
        if (rateDto != null && !rateDto.getRates().isEmpty()) {
            exchangeRate = Optional.ofNullable(rateDto.getRates().get(0).getMid())
                    .orElseThrow(() -> new DataNotFoundException("Exchange rate not found"));
        } else {
            throw new DataNotFoundException("Exchange rate not found");
        }
        return exchangeRate;
    }

    public List<CurrencyInfoDto> currencyInfoApiRequest(String code){
        ResponseEntity<CurrencyInfoDto[]> infoResponse;
        List<CurrencyInfoDto> infoList;

        try {
            ParameterizedTypeReference<CurrencyInfoDto[]> responseType = new ParameterizedTypeReference<>() {};
            infoResponse = restTemplate.exchange(infoUrl + "{code}/", HttpMethod.GET, null,
                    responseType, code);
        } catch (HttpStatusCodeException exception) {
            throw new ApiNotFoundException("Currency info API not found");
        }

        if (infoResponse.getStatusCode().is2xxSuccessful() &&
                (infoResponse.getBody() != null && infoResponse.getBody().length > 0)) {
            infoList = Arrays.asList(infoResponse.getBody());
        } else {
            throw new DataNotFoundException("Currency info not found");
        }
        return infoList;
    }
}

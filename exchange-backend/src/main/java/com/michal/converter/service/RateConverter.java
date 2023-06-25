package com.michal.converter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.michal.converter.dto.RequestDto;
import com.michal.converter.dto.ResponseDto;
import com.michal.converter.web_service.CurrencyWebService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
public class RateConverter {
    @Value("${webapi.basic-currency:pln}")
    private String basicCurrency;
    private final ObjectMapper objectMapper;

    public ResponseDto convert(RequestDto data, CurrencyWebService currencyWebService) {
        BigDecimal exchangeRate;
        BigDecimal result;

        if (data.getInputCurrency().equalsIgnoreCase(data.getOutputCurrency())) {
            exchangeRate = BigDecimal.ONE;
        }else if (data.getInputCurrency().equalsIgnoreCase(basicCurrency)){
            exchangeRate = (BigDecimal.ONE).divide(
                    currencyWebService.getCurrencyRate(data.getOutputCurrency()), 4, RoundingMode.HALF_UP);
        }else if (data.getOutputCurrency().equalsIgnoreCase(basicCurrency)){
            exchangeRate = currencyWebService.getCurrencyRate(data.getInputCurrency());
        } else {
            BigDecimal inputRate = currencyWebService.getCurrencyRate(data.getInputCurrency());
            BigDecimal outputRate = currencyWebService.getCurrencyRate(data.getOutputCurrency());
            exchangeRate = inputRate.divide(outputRate, 4, RoundingMode.HALF_UP);
        }

        result = exchangeRate.multiply(data.getValue());
        return ResponseDto.builder()
                .result(objectMapper
                        .convertValue(result.setScale(2, RoundingMode.CEILING), String.class))
                .exchangeRate(objectMapper.convertValue(exchangeRate, String.class))
                .build();
    }
}

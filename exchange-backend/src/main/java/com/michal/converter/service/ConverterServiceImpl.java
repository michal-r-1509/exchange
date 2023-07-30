package com.michal.converter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.michal.converter.dto.RequestDto;
import com.michal.converter.dto.ResponseDto;
import com.michal.converter.web_service.CurrencyWebService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
public class ConverterServiceImpl implements ConverterService, ApplicationContextAware {

    private final CurrencyWebService currencyWebService;
    private RateConverter converter;
    private ObjectMapper objectMapper;

    @Override
    public ResponseDto convert(RequestDto data) {
        BigDecimal exchangeRate = converter.convert(data, currencyWebService);
        BigDecimal result = exchangeRate.multiply(data.getValue());
        return ResponseDto.builder()
                .result(objectMapper
                        .convertValue(result.setScale(2, RoundingMode.CEILING), String.class))
                .exchangeRate(exchangeRate.toPlainString())
                .build();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        converter = applicationContext.getBean(RateConverter.class);
        objectMapper = applicationContext.getBean(ObjectMapper.class);
    }
}

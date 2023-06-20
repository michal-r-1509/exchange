package com.michal.converter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.michal.converter.dto.RequestDto;
import com.michal.converter.dto.ResponseDto;
import com.michal.converter.web_service.CurrencyWebService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NotScheduledServiceImpl implements ConverterService, ApplicationContextAware {
    @Value("${webapi.basic-currency:pln}")
    private String basicCurrency;
    private ObjectMapper objectMapper;
    private CurrencyWebService currencyWebService;

    public ResponseDto convert(RequestDto data) {
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        objectMapper = applicationContext.getBean(ObjectMapper.class);
        currencyWebService = applicationContext.getBean("webServiceNotScheduled", CurrencyWebService.class);
    }
}

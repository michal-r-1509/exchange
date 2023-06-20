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

public class ScheduledServiceImpl implements ConverterService, ApplicationContextAware {
    @Value("${webapi.basic-currency:pln}")
    private String basicCurrency;
    private ObjectMapper objectMapper;
    private CurrencyWebService currencyWebService;

    public ResponseDto convert(RequestDto data) {
        BigDecimal exchangeRate;
        BigDecimal result;
        String inputCurrency = data.getInputCurrency();
        String outputCurrency = data.getOutputCurrency();

        if (inputCurrency.equalsIgnoreCase(outputCurrency)) {
            exchangeRate = BigDecimal.ONE;
        }else if (inputCurrency.equalsIgnoreCase(basicCurrency)){
            currencyWebService.setCurrency(outputCurrency);
            exchangeRate = (BigDecimal.ONE).divide(
                    currencyWebService.getRate(outputCurrency), 4, RoundingMode.HALF_UP);
        }else if (outputCurrency.equalsIgnoreCase(basicCurrency)){
            currencyWebService.setCurrency(inputCurrency);
            exchangeRate = currencyWebService.getRate(inputCurrency);
        } else {
            currencyWebService.setCurrency(inputCurrency);
            BigDecimal inputRate = currencyWebService.getRate(inputCurrency);
            currencyWebService.setCurrency(outputCurrency);
            BigDecimal outputRate = currencyWebService.getRate(outputCurrency);
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
        currencyWebService = applicationContext.getBean("webServiceScheduled", CurrencyWebService.class);
    }
}

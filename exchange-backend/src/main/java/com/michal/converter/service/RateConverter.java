package com.michal.converter.service;

import com.michal.converter.dto.RequestDto;
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

    public BigDecimal convert(RequestDto data, CurrencyWebService currencyWebService) {
        BigDecimal exchangeRate;
        if (data.getInputCurrency().equalsIgnoreCase(data.getOutputCurrency())) {
            exchangeRate = BigDecimal.ONE;
        }else if (data.getInputCurrency().equalsIgnoreCase(basicCurrency)){
            exchangeRate = (BigDecimal.ONE).divide(
                    currencyWebService.getExchangeRate(data.getOutputCurrency()), 4, RoundingMode.HALF_UP);
        }else if (data.getOutputCurrency().equalsIgnoreCase(basicCurrency)){
            exchangeRate = currencyWebService.getExchangeRate(data.getInputCurrency());
        } else {
            BigDecimal inputRate = currencyWebService.getExchangeRate(data.getInputCurrency());
            BigDecimal outputRate = currencyWebService.getExchangeRate(data.getOutputCurrency());
            exchangeRate = inputRate.divide(outputRate, 4, RoundingMode.HALF_UP);
        }
        return exchangeRate.setScale(4, RoundingMode.CEILING);
    }
}

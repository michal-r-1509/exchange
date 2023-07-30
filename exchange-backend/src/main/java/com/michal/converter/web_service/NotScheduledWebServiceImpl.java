package com.michal.converter.web_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Service("notScheduledWebService")
public class NotScheduledWebServiceImpl implements CurrencyWebService {

    private final ApiRequest apiRequest;

    @Override
    public BigDecimal getExchangeRate(String currency) {
        BigDecimal result = apiRequest.exchangeRateApiRequest(currency);
        log.info("got exchange rate " + currency + " " + result);
        return result;
    }
}

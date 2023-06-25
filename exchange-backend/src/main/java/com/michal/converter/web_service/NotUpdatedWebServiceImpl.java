package com.michal.converter.web_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Service("notUpdatedCacheService")
public class NotUpdatedWebServiceImpl implements CurrencyWebService {

    private final ApiRequest apiRequest;

    @Override
    @Cacheable(value = "rates")
    public BigDecimal getCurrencyRate(String currency) {
        BigDecimal result = apiRequest.apiRequest(currency);
        log.info("got exchange rate " + currency + " " + result);
        return result;
    }

    @Override
    public BigDecimal getRate(String currency) {
        return null;
    }

    @Override
    public void setCurrency(String currency) {
    }
}

package com.michal.converter.web_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service("webServiceScheduled")
public class ScheduledWebServiceImpl implements CurrencyWebService {

    private final ApiRequest apiRequest;
    private String currency;
    private final Map<String, BigDecimal> rates = new HashMap<>();

    @Override
    public BigDecimal getCurrencyRate(String currency) {
        return null;
    }

    @Override
    public BigDecimal getRate(String currency) {
        return rates.get(currency);
    }

    @Override
    public void setCurrency(String currency) {
        if (rates.containsKey(currency)) {
            return;
        } else {
            this.currency = currency;
            getCurrencyRate();
        }
    }

    public void getCurrencyRate() {
        if (currency == null) {
            return;
        }
        BigDecimal result = apiRequest.apiRequest(currency);
        if (!rates.containsKey(this.currency)) {
            rates.put(this.currency, result);
            log.info("got new map entry: " + currency + " " + result);
        }
    }

    @CacheEvict(value = "rates", allEntries = true)
    @Scheduled(fixedRateString = "${cache.update-interval-in-seconds:30}", timeUnit = TimeUnit.SECONDS)
    public void updateCurrencyRate() {
        if (currency == null || rates.isEmpty()) {
            return;
        }
        rates.forEach((key, value) -> {
            BigDecimal v = apiRequest.apiRequest(key);
            rates.replace(key, v);
            log.info("update map entry: " + key + " " + v.toString());
        });
    }

    @Scheduled(fixedRateString = "${cache.clear-interval-in-seconds:179}",
            initialDelayString = "${cache.clear-delay-in-seconds:179}", timeUnit = TimeUnit.SECONDS)
    public void clearTable() {
        if (this.rates.isEmpty() && this.currency == null) {
            return;
        }
        this.rates.clear();
        this.currency = null;
        log.info("TABLE CLEARED");
    }
}

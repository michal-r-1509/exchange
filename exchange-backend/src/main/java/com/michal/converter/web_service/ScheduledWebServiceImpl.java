package com.michal.converter.web_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service("scheduledWebService")
public class ScheduledWebServiceImpl implements CurrencyWebService {

    private final ApiRequest apiRequest;
    private final Map<String, BigDecimal> rates = new HashMap<>();

    @Override
    public BigDecimal getExchangeRate(String currency) {
        setCurrency(currency);
        return rates.get(currency);
    }

    private void setCurrency(String currency) {
        if (rates.containsKey(currency)) {
            return;
        }
        BigDecimal result = apiRequest.exchangeRateApiRequest(currency);
        rates.put(currency, result);
        log.info("got new cache table entry: " + currency + " " + result);

    }

    @Scheduled(fixedRateString = "${cache.update-interval-in-seconds:30}", timeUnit = TimeUnit.SECONDS)
    public void updateCurrencyRate() {
        if (rates.isEmpty()) {
            return;
        }
        rates.forEach((key, value) -> {
            BigDecimal v = apiRequest.exchangeRateApiRequest(key);
            rates.replace(key, v);
            log.info("update cache table entry: " + key + " " + v.toString());
        });
    }

    @Configuration
    @ConditionalOnProperty(prefix = "cache", name = "cache-table", havingValue = "clear", matchIfMissing = true)
    class TableCache {
        @Scheduled(fixedRateString = "${cache.clear-interval-in-seconds:179}",
                initialDelayString = "${cache.clear-interval-in-seconds:179}", timeUnit = TimeUnit.SECONDS)
        public void clearTable() {
            if (ScheduledWebServiceImpl.this.rates.isEmpty()) {
                return;
            }
            ScheduledWebServiceImpl.this.rates.clear();
            log.info("CACHE TABLE CLEARED");
        }

    }
}

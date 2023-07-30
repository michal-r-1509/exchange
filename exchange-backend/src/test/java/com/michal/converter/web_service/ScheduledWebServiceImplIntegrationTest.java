package com.michal.converter.web_service;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class ScheduledWebServiceImplIntegrationTest {

    @MockBean
    private ApiRequest apiRequest;
    private final Map<String, BigDecimal> rates = new HashMap<>();
    @Autowired
    private ScheduledWebServiceImpl underTest;
    private Field ratesField;

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        ratesField = underTest.getClass().getDeclaredField("rates");
        ratesField.setAccessible(true);
    }

    @Test
    void getExchangeRate_whenInRatesMapIsNotExistCurrency_thenItMakeRequestAndSaves() {
        String currency = "usd";
        rates.put(currency, BigDecimal.TEN);
        setRates();

        when(apiRequest.exchangeRateApiRequest(anyString())).thenReturn(BigDecimal.ONE, BigDecimal.ZERO);
        Awaitility.await()
                .atLeast(9, TimeUnit.SECONDS)
                .atMost(11, TimeUnit.SECONDS)
                .until(() -> rates.get(currency).compareTo(BigDecimal.ZERO) == 0);

        verify(apiRequest, times(2)).exchangeRateApiRequest(anyString());
        assertThat(rates.get(currency).compareTo(BigDecimal.ZERO)).isEqualTo(0);
    }

    @Test
    void getExchangeRate_whenInRatesMapExistCurrency_thenMethodReturns() {
        String currency = "usd";
        rates.put(currency, BigDecimal.TEN);
        setRates();

        BigDecimal exchangeRate = underTest.getExchangeRate(currency);

        assertThat(rates).hasSize(1);
        assertThat(BigDecimal.TEN.compareTo(exchangeRate)).isEqualTo(0);
        verify(apiRequest, never()).exchangeRateApiRequest(currency);
    }

    @Test
    void updateCurrencyRate_returnsWhenRatesMapEmpty() {
        setRates();

        underTest.updateCurrencyRate();

        assertThat(rates).isEmpty();
        verify(apiRequest, never()).exchangeRateApiRequest("usd");
    }

    @Test
    void updateCurrencyRate_updatesRatesWhenRatesMapNotEmpty() {
        rates.put("usd", BigDecimal.TEN);
        rates.put("cad", BigDecimal.ONE);
        setRates();

        when(apiRequest.exchangeRateApiRequest(anyString()))
                .thenReturn(BigDecimal.valueOf(20), BigDecimal.valueOf(2));

        underTest.updateCurrencyRate();

        assertThat(rates).hasSize(2);
        assertThat(rates.get("usd").compareTo(BigDecimal.valueOf(20))).isEqualTo(0);
        assertThat(rates.get("cad").compareTo(BigDecimal.valueOf(2))).isEqualTo(0);
        verify(apiRequest, times(2)).exchangeRateApiRequest(anyString());
    }

    private void setRates(){
        try {
            ratesField.set(underTest, rates);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
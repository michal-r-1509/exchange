package com.michal.converter.web_service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotScheduledWebServiceImplTest {

    @Mock
    private ApiRequest apiRequest;

    private NotScheduledWebServiceImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = new NotScheduledWebServiceImpl(apiRequest);
    }

    @Test
    void getExchangeRate_returnsDifferentValues_everyTimeWhenInvokedWithSameArguments_andCallRequestApiTwoTimes(){
        String currency = "usd";

        when(apiRequest.exchangeRateApiRequest(currency)).thenReturn(BigDecimal.ONE, BigDecimal.TEN);

        BigDecimal rateFirst = underTest.getExchangeRate(currency);
        BigDecimal rateSecond = underTest.getExchangeRate(currency);

        assertThat(BigDecimal.ONE.compareTo(rateFirst)).isEqualTo(0);
        assertThat(BigDecimal.TEN.compareTo(rateSecond)).isEqualTo(0);
        verify(apiRequest, times(2)).exchangeRateApiRequest(currency);
    }
}
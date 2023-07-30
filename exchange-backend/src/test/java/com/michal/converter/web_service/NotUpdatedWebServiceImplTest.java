package com.michal.converter.web_service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
class NotUpdatedWebServiceImplTest {

    @MockBean
    private ApiRequest apiRequest;

    @Autowired
    private NotUpdatedWebServiceImpl underTest;

    @Test
    @DisplayName("returns equal values and call ApiRequest once at first time")
    void getExchangeRate_returnsEqualValues_everyTimeWhenInvokedWithSameArguments_andCallRequestApiOnce() {
        String currency = "usd";

        when(apiRequest.exchangeRateApiRequest(currency)).thenReturn(BigDecimal.ONE);

        BigDecimal rateFirst = underTest.getExchangeRate(currency);
        BigDecimal rateSecond = underTest.getExchangeRate(currency);

        assertThat(rateFirst.compareTo(rateSecond)).isEqualTo(0);
        verify(apiRequest, times(1)).exchangeRateApiRequest(currency);
    }
}
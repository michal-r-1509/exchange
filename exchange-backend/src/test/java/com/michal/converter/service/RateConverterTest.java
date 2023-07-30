package com.michal.converter.service;

import com.michal.converter.dto.RequestDto;
import com.michal.converter.web_service.CurrencyWebService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateConverterTest {

    @Mock
    private CurrencyWebService webService;
    private RateConverter underTest;


    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        underTest = new RateConverter();

        String basicCurrency = "pln";
        Field field = underTest.getClass().getDeclaredField("basicCurrency");
        field.setAccessible(true);
        field.set(underTest, basicCurrency);
    }

    @Test
    void convert_returnsBigDecimalOne_whenCurrenciesEquals() {
        String inputCurrency = "usd";
        String outputCurrency = "USD";
        RequestDto data = new RequestDto();
        data.setInputCurrency(inputCurrency);
        data.setOutputCurrency(outputCurrency);

        BigDecimal converted = underTest.convert(data, webService);

        assertThat(BigDecimal.ONE.compareTo(converted)).isEqualTo(0);
    }

    @Test
    void convert_returnsBigDecimalTen_whenInputCurrencyEqualsBasic() {
        String inputCurrency = "pln";
        RequestDto data = new RequestDto();
        data.setInputCurrency(inputCurrency);

        when(webService.getExchangeRate(null)).thenReturn(BigDecimal.valueOf(0.1));
        BigDecimal converted = underTest.convert(data, webService);

        assertThat(BigDecimal.TEN.compareTo(converted)).isEqualTo(0);
    }

    @Test
    void convert_returnsBigDecimalTen_whenOutputCurrencyEqualsBasic() {
        String inputCurrency = "usd";
        String outputCurrency = "pln";
        RequestDto data = new RequestDto();
        data.setInputCurrency(inputCurrency);
        data.setOutputCurrency(outputCurrency);

        when(webService.getExchangeRate(anyString())).thenReturn(BigDecimal.TEN);
        BigDecimal converted = underTest.convert(data, webService);

        assertThat(BigDecimal.TEN.compareTo(converted)).isEqualTo(0);
    }

    @Test
    void convert_returnsBigDecimalTen_whenInputAndOutputNotEqualsBasic() {
        String inputCurrency = "usd";
        String outputCurrency = "gbp";
        RequestDto data = new RequestDto();
        data.setInputCurrency(inputCurrency);
        data.setOutputCurrency(outputCurrency);

        when(webService.getExchangeRate(anyString())).thenReturn(BigDecimal.TEN);
        BigDecimal converted = underTest.convert(data, webService);

        assertThat(BigDecimal.ONE.compareTo(converted)).isEqualTo(0);
    }
}
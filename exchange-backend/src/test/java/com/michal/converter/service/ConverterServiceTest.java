package com.michal.converter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.michal.converter.dto.RequestDto;
import com.michal.converter.dto.ResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConverterServiceTest {

    @Mock
    private RateConverter rateConverter;
    @Mock
    private ObjectMapper objectMapper;
    private ConverterService underTest;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        underTest = new ConverterServiceImpl(null);

        Field converter = underTest.getClass().getDeclaredField("converter");
        converter.setAccessible(true);
        converter.set(underTest, rateConverter);

        Field mapper = underTest.getClass().getDeclaredField("objectMapper");
        mapper.setAccessible(true);
        mapper.set(underTest, objectMapper);
    }

    @Test
    void convert_returnResponse() {
        RequestDto requestDto = new RequestDto();
        requestDto.setInputCurrency("pln");
        requestDto.setOutputCurrency("usd");
        requestDto.setValue(BigDecimal.valueOf(3L));

        when(rateConverter.convert(eq(requestDto), any())).thenReturn(BigDecimal.valueOf(12L));
        when(objectMapper.convertValue(any(), eq(String.class))).thenReturn("36");

        ResponseDto response = underTest.convert(requestDto);

        assertThat(response.getExchangeRate()).isEqualTo("12");
        assertThat(response.getResult()).isEqualTo("36");
    }
}
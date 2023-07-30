package com.michal.converter.currency_info.service;

import com.michal.converter.currency_info.dto.CurrencyInfoDto;
import com.michal.converter.currency_info.dto.InfoRequestDto;
import com.michal.converter.currency_info.dto.InfoResponseDto;
import com.michal.converter.web_service.ApiRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyInfoServiceTest {

    @Mock
    private ApiRequest apiRequest;
    @InjectMocks
    private CurrencyInfoService underTest;
    private Field cacheField;

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        cacheField = underTest.getClass().getDeclaredField("cache");
        cacheField.setAccessible(true);
    }

    @Test
    void getCurrencyInfo_withNonCachedValue_shouldFetchAndCacheData() {
        String code = "usd";
        List<CurrencyInfoDto> apiResponse = new ArrayList<>();
        apiResponse.add(new CurrencyInfoDto());
        InfoRequestDto requestDto = new InfoRequestDto();
        requestDto.setCode(code);

        when(apiRequest.currencyInfoApiRequest(code)).thenReturn(apiResponse);
        underTest.getCurrencyInfo(requestDto);

        verify(apiRequest).currencyInfoApiRequest(code);
        assertEquals(apiResponse.size(), 1);
    }

    @Test
    void getCurrencyInfo_withCachedValue_shouldReturnDataFromCache() throws IllegalAccessException {
        String code = "usd";
        List<InfoResponseDto> cachedData = new ArrayList<>();
        InfoResponseDto infoResponseDto = new InfoResponseDto();
        infoResponseDto.setCode(code);
        cachedData.add(infoResponseDto);
        Map<String, List<InfoResponseDto>> cache = new HashMap<>();
        cache.put(code, cachedData);
        InfoRequestDto infoRequestDto = new InfoRequestDto();
        infoRequestDto.setCode(code);
        cacheField.set(underTest, cache);

        InfoResponseDto currencyInfo = underTest.getCurrencyInfo(infoRequestDto);

        verify(apiRequest, never()).currencyInfoApiRequest(code);
        assertEquals(currencyInfo.getCode(), code);
    }
}
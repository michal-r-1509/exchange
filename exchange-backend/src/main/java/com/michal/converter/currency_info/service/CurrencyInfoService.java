package com.michal.converter.currency_info.service;

import com.michal.converter.currency_info.dto.CurrencyInfoDto;
import com.michal.converter.currency_info.dto.InfoRequestDto;
import com.michal.converter.currency_info.dto.InfoResponseDto;
import com.michal.converter.web_service.ApiRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class CurrencyInfoService {

    private final ApiRequest apiRequest;

    private final Map<String, List<InfoResponseDto>> cache = new HashMap<>();

    public InfoResponseDto getCurrencyInfo(InfoRequestDto code) {
        Random random = new Random();
        List<InfoResponseDto> infoDtoList;
        if (!cache.containsKey(code.getCode())){
            infoDtoList = mapToResponse(apiRequest.currencyInfoApiRequest(code.getCode()), code.getCode());
            cache.put(code.getCode(), infoDtoList);
        }else {
            infoDtoList = cache.get(code.getCode());
        }
        int rnd = random.nextInt(infoDtoList.size());
        return infoDtoList.get(rnd);
    }

    private List<InfoResponseDto> mapToResponse(List<CurrencyInfoDto> data, String code) {
        List<InfoResponseDto> responseDtos = new ArrayList<>();
        for (CurrencyInfoDto entity : data) {
            responseDtos.add(new InfoResponseDto(entity, code));
        }
        return responseDtos;
    }

    @Scheduled(fixedRateString = "${cache.clear-interval-in-seconds:179}", timeUnit = TimeUnit.SECONDS)
    public void clearTable() {
        if (cache.isEmpty()) {
            return;
        }
        cache.clear();
        log.info("CACHE TABLE OF INFO CLEARED");
    }
}

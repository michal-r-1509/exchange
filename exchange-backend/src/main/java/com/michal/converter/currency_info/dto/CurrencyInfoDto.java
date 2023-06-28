package com.michal.converter.currency_info.dto;

import com.michal.converter.currency_info.web_service_dto.FlagDto;
import com.michal.converter.currency_info.web_service_dto.NameDto;
import lombok.Getter;

@Getter
public class CurrencyInfoDto {
    private NameDto name;
    private String region;
    private String subregion;
    private FlagDto flags;
}

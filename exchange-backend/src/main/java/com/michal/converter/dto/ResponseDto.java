package com.michal.converter.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResponseDto {
    private String result;
    private String exchangeRate;
}

package com.michal.converter.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ResponseDto {
    private String result;
    private String exchangeRate;
}

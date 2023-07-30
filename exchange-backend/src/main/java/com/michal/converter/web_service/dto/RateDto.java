package com.michal.converter.web_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RateDto {
    private List<Rates> rates;
}

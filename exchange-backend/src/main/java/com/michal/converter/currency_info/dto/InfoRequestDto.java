package com.michal.converter.currency_info.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class InfoRequestDto {
    @NotBlank
    @Size(min = 3, max = 3)
    private String code;
}

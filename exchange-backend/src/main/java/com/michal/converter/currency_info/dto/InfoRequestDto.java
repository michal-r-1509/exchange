package com.michal.converter.currency_info.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InfoRequestDto {
    @NotBlank
    @Pattern(regexp = "[A-Za-z]{3}", message = "Currency code must be a 3-letter alphabetic code")
    private String code;
}

package com.michal.converter.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RequestDto {
    @NotBlank
    @Pattern(regexp = "[A-Za-z]{3}", message = "Currency code must be a 3-letter alphabetic code")
    private String inputCurrency;
    @NotBlank
    @Pattern(regexp = "[A-Za-z]{3}", message = "Currency code must be a 3-letter alphabetic code")
    private String outputCurrency;
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    @DecimalMin("0.0")
    @DecimalMax("999999999.99")
    private BigDecimal value;
}

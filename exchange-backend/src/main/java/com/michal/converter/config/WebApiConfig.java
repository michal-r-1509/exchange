package com.michal.converter.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "webapi")
public class WebApiConfig {
    @NotBlank
    private String currencyInfoUrl;
    @NotBlank
    private String currencyRequestUrl;
    private String basicCurrency;
}

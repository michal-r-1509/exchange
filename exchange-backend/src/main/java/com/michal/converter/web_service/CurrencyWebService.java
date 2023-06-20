package com.michal.converter.web_service;

import java.math.BigDecimal;

public interface CurrencyWebService {
    BigDecimal getCurrencyRate(String currency);
    BigDecimal getRate(String currency);
    void setCurrency(String currency);
}

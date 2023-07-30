package com.michal.converter.web_service;

import java.math.BigDecimal;

public interface CurrencyWebService {
    BigDecimal getExchangeRate(String currency);
}

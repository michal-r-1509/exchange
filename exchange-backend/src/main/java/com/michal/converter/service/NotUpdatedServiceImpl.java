package com.michal.converter.service;

import com.michal.converter.dto.RequestDto;
import com.michal.converter.dto.ResponseDto;
import com.michal.converter.web_service.CurrencyWebService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class NotUpdatedServiceImpl implements ConverterService, ApplicationContextAware {
    private CurrencyWebService currencyWebService;
    private RateConverter converter;

    public ResponseDto convert(RequestDto data) {
        return converter.convert(data, currencyWebService);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        currencyWebService = applicationContext.getBean("notUpdatedCacheService", CurrencyWebService.class);
        converter = applicationContext.getBean(RateConverter.class);
    }
}

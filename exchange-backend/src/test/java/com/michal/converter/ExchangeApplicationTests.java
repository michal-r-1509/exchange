package com.michal.converter;

import com.michal.converter.api.ConverterController;
import com.michal.converter.currency_info.api.CurrencyInfoController;
import com.michal.converter.currency_info.service.CurrencyInfoService;
import com.michal.converter.service.ConverterService;
import com.michal.converter.service.RateConverter;
import com.michal.converter.web_service.ApiRequest;
import com.michal.converter.web_service.CurrencyWebService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ExchangeApplicationTests {

	@Autowired
	private ConverterService converterService;

	@Autowired
	private ConverterController converterController;

	@Autowired
	private CurrencyInfoService currencyInfoService;

	@Autowired
	private CurrencyInfoController currencyInfoController;

	@Autowired
	private ApiRequest apiRequest;

	@Autowired
	private RateConverter rateConverter;

	@Qualifier("scheduledWebService")
	@Autowired
	private CurrencyWebService scheduledWebService;

	@Qualifier("notScheduledWebService")
	@Autowired
	private CurrencyWebService notScheduledWebService;

	@Qualifier("notUpdatedCacheWebService")
	@Autowired
	private CurrencyWebService notUpdatedWebService;

	@Test
	void contextLoads() {
		assertThat(converterService).isNotNull();
		assertThat(converterController).isNotNull();
		assertThat(apiRequest).isNotNull();
		assertThat(currencyInfoService).isNotNull();
		assertThat(currencyInfoController).isNotNull();
		assertThat(rateConverter).isNotNull();
		assertTrue(scheduledWebService != null || notScheduledWebService != null
				|| notUpdatedWebService != null);
	}

}

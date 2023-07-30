package com.michal.converter.config;

import com.michal.converter.service.*;
import com.michal.converter.web_service.NotScheduledWebServiceImpl;
import com.michal.converter.web_service.NotUpdatedWebServiceImpl;
import com.michal.converter.web_service.ScheduledWebServiceImpl;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Slf4j
@Configuration
//If we don't use @Configuration in the POJO,
// then need to add @EnableConfigurationProperties(CacheProperties.class) in the main Spring application class
@ConfigurationProperties(prefix = "cache")
public class CacheConfig {
    private Scheduling schedulingMethod;
    private TableClearing cacheTable;
    private int updateIntervalInSeconds;
    private int clearIntervalInSeconds;

    @Bean
    @ConditionalOnProperty(prefix = "cache", name = "scheduling-method", havingValue = "not_scheduled")
    public ConverterService notScheduledService(NotScheduledWebServiceImpl webService) {
        log.info("Application runs in NOT SCHEDULED mode");
        return new ConverterServiceImpl(webService);
    }

    @Bean
    @ConditionalOnProperty(prefix = "cache", name = "scheduling-method",
            havingValue = "scheduled", matchIfMissing = true)
    public ConverterService scheduledService(ScheduledWebServiceImpl webService) {
        log.info("Application runs in SCHEDULED (default) mode");
        return new ConverterServiceImpl(webService);
    }

    @Bean
    @ConditionalOnProperty(prefix = "cache", name = "scheduling-method", havingValue = "not_updated")
    public ConverterService notUpdatedRateService(NotUpdatedWebServiceImpl webService) {
        log.info("Application runs in NOT UPDATED RATE mode");
        return new ConverterServiceImpl(webService);
    }

    public enum Scheduling {
        SCHEDULED,
        NOT_SCHEDULED,
        NOT_UPDATED
    }

    public enum TableClearing{
        UPDATE,
        CLEAR
    }
}

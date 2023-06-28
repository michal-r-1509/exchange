package com.michal.converter.config;

import com.michal.converter.service.ConverterService;
import com.michal.converter.service.NotUpdatedServiceImpl;
import com.michal.converter.service.NotScheduledServiceImpl;
import com.michal.converter.service.ScheduledServiceImpl;
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
    private int clearDelayInSeconds;

    @Bean
    @ConditionalOnProperty(prefix = "cache", name = "scheduling-method", havingValue = "not_scheduled")
    public ConverterService notScheduledService() {
        log.info("Application runs in NOT SCHEDULED mode");
        return new NotScheduledServiceImpl();
    }

    @Bean
    @ConditionalOnProperty(prefix = "cache", name = "scheduling-method",
            havingValue = "scheduled", matchIfMissing = true)
    public ConverterService scheduledService() {
        log.info("Application runs in SCHEDULED (default) mode");
        return new ScheduledServiceImpl();
    }

    @Bean
    @ConditionalOnProperty(prefix = "cache", name = "scheduling-method", havingValue = "not_updated")
    public ConverterService lastRateService() {
        log.info("Application runs in LAST RATE mode");
        return new NotUpdatedServiceImpl();
    }

    public enum Scheduling {
        SCHEDULED,
        NOT_SCHEDULED,
        NOT_UPDATED
    }

    public enum TableClearing{
        UPDATE,
        PURGE
    }
}

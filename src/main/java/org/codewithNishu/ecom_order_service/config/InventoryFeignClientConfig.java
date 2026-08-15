package org.codewithNishu.ecom_order_service.config;

import java.time.Duration;
import java.util.UUID;

import org.codewithNishu.ecom_order_service.exceptions.CustomErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;

@Configuration
public class InventoryFeignClientConfig {

    @Bean
    public Logger.Level feignInvLoggerLevel(){
        return Logger.Level.FULL;
    }

    @Bean
    public Request.Options options(){
        return new Request.Options(Duration.ofMillis(3000),Duration.ofMillis(5000), true);
    }
    @Bean
    public Retryer retryer(){
        return new Retryer.Default(1L, 2L, 3);
    }

    @Bean
    public RequestInterceptor requestInterceptor(){
        return requestTemplate -> {
            requestTemplate.header("x-Correlation-Id", UUID.randomUUID().toString());
        };
    }
    @Bean
    public ErrorDecoder errorDecoder(){
        return new CustomErrorDecoder();
    }
    
    @Bean
    public Encoder encoder(){
     return new CustomInventoryEncoder();
    }

}

package com.skyb.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.skyb.service.RandomGenerator;

@Configuration
public class TestConfiguration {

    /**
     * Fix random generator to return a specific value for integration testing
     * 
     * @return
     */
    @Bean
    @Primary
    public RandomGenerator randomGenerator() {
        return new RandomGenerator() {

            @Override
            public int generateRandomInt(int min, int max) {
                return 1;
            }
        };
    }
}

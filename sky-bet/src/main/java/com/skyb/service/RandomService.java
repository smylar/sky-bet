package com.skyb.service;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

/**
 * Service for generating random things
 * 
 * @author paul
 *
 */
@Service
public class RandomService implements RandomGenerator {

    /* (non-Javadoc)
     * @see com.skyb.service.RandomGenerator#generateRandomInt(int, int)
     */
    @Override
    public int generateRandomInt(int min, int max) {
       return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}

package com.skyb.service;

/**
 * Generator of random things
 * 
 * @author paul
 *
 */
public interface RandomGenerator {

    /**
     * Generate an integer between the min and max values (inclusive)
     * 
     * @return
     */
    int generateRandomInt(int min, int max);

}
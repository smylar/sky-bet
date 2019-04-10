package com.skyb.data;

import java.util.Optional;
import java.util.Set;

import com.skyb.entity.Bet;

import lombok.Value;

/**
 * Defines the request made via HTTP POST to place/change a bet
 * 
 * @author paul
 *
 */
@Value
public class NewBet {
    private long gameId;
    private long customerId;
    private Bet bet;
    private double value;
    private Optional<Set<Integer>> numbers;
    
}

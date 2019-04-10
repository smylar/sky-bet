package com.skyb.entity;

import java.util.Collection;
import java.util.Set;

import com.skyb.controller.exception.RequestException;

/**
 * Defines and tests all the bets that can be made
 * 
 * @author paul
 *
 */
public enum Bet {
    STRAIGHTUP(36, 1, Set.of()),
    SPLIT(18, 2, Set.of()),
    CORNER(9, 4, Set.of()),
    RED(2, 0, Set.of(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23,25, 27, 30, 32, 34, 36)),
    BLACK(2, 0, Set.of(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35)),
    COLUMN1(3, 0, Set.of(1, 4, 7, 10, 13, 16, 19, 22, 25, 28, 31, 34)),
    COLUMN2(3, 0, Set.of(2, 5, 8, 11, 14, 17, 20, 23, 26, 29, 32, 35)),
    COLUMN3(3, 0, Set.of(3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36)),
    //and the rest
    ;
    
    private int odds;
    private int numbersRequired;
    private Set<Integer> preset;
    
    private Bet(int odds, int numbersRequired, Set<Integer> preset) {
        this.odds = odds;
        this.preset = preset;
        this.numbersRequired = numbersRequired;
    }
    
    public double calculateWinnings(final int number, final double betValue, final Set<Integer> numbersBet) {
        Set<Integer> numbersToCheck = preset.isEmpty() ? numbersBet : preset;
        
        return numbersToCheck.stream()
                             .dropWhile(n -> n != number)
                             .findFirst()
                             .map(n -> betValue * odds)
                             .orElse(0d);
    }
    
    public boolean isBetAllowed(Collection<Integer> numbersBet) {
        if (numbersRequired > 0 && 
                (numbersBet.size() != numbersRequired
                || numbersBet.stream().anyMatch(n -> n < 0 || n > 36))) {
            throw new RequestException("Illegal bet!");
        }
        
        return true;
    }

}

package com.skyb.service;

import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyb.controller.exception.RequestException;
import com.skyb.data.NewBet;
import com.skyb.entity.BetStatus;
import com.skyb.entity.GameStatus;
import com.skyb.entity.RouletteBet;
import com.skyb.repository.GameStateRespository;
import com.skyb.repository.RouletteRepository;

/**
 * Service handling everything to do with Roulette
 * 
 * @author paul
 *
 */
@Service
public class RouletteService {
    
    @Autowired
    private RouletteRepository rouletteRepository;
    
    @Autowired
    private GameStateRespository gameStateRepository;
    
    @Autowired
    private RandomGenerator random;
    
    @Autowired
    private ObjectMapper mapper;

    /**
     * Place a new bet
     * 
     * @param bet   - The bet being made
     * @return      - The new {@link RouletteBet} entity
     */
    public RouletteBet placeBet(final NewBet bet) {
        final Set<Integer> betNumbers = bet.getNumbers().orElse(Set.of());
        return gameStateRepository.findById(bet.getGameId())
                                  .filter(gs -> gs.getStatus() == GameStatus.OPEN && bet.getBet().isBetAllowed(betNumbers))
                                  .map(gs -> RouletteBet.builder().betType(bet.getBet())
                                                                  .customerId(bet.getCustomerId())
                                                                  .gameId(bet.getGameId())
                                                                  .status(BetStatus.PENDING)
                                                                  .value(bet.getValue())
                                                                  .numberSelection(betNumbers.toString()).build())
                                  .map(rouletteRepository::save)
                                  .orElseThrow(() -> new RequestException("Could not place bet"));
    }
    
    /**
     * Get the details of an existing bet
     * 
     * @param id            - The ID of the  bet
     * @param customerId    - The ID of the customer
     * @return              - The existing {@link RouletteBet} entity
     */
    public RouletteBet getBet(final long id, final long customerId) {
        return rouletteRepository.findById(id)
                                 .filter(bet -> bet.getCustomerId() == customerId)
                                 .orElseThrow(() -> new RequestException("Bet not found"));
    }
    
    /**
     * Cancel an existing bet
     * 
     * @param id            - The ID of the bet
     * @param customerId    - The ID of the customer
     * @return              - True, if the bet could be cancelled, false, otherwise
     */
    public boolean cancelBet(final long id, final long customerId) {
        return rouletteRepository.findById(id)
                                 .filter(bet -> bet.getCustomerId() == customerId && BetStatus.PENDING == bet.getStatus())
                                 .flatMap(bet -> gameStateRepository.findById(bet.getGameId()))
                                 .map(gs -> GameStatus.OPEN == gs.getStatus())
                                 .filter(b -> b)
                                 .map(b -> {
                                     rouletteRepository.deleteById(id);
                                     return b;
                                 })
                                 .orElse(false);
    }
    
    /**
     * Update all pending bets for the given game, i.e. whether they have won or lost
     * 
     * @param gameId    - ID of the game to resolve
     * @return          - The winning number
     */
    @Transactional
    public int resolveRound(final long gameId) {
        //possibly should go in another service as this is more an admin function, not specific to a user
        int result = random.generateRandomInt(0, 36);
        
        rouletteRepository.getByGameIdAndStatus(gameId, BetStatus.PENDING)
                          .stream()
                          .map(bet -> processBet(bet, result))
                          .forEach(rouletteRepository::save);                     
        
        return result;
    }
    
    private RouletteBet processBet(RouletteBet bet, int winningNumber) {
        try {
            Set<Integer> numbers = Set.of(mapper.readValue(bet.getNumberSelection(), Integer[].class));
            double winnings = bet.getBetType().calculateWinnings(winningNumber, bet.getValue(), numbers);
            return bet.toBuilder()
                      .winningNumber(winningNumber)
                      .winnings(winnings)
                      .status(winnings == 0 ? BetStatus.LOST : BetStatus.WON)
                      .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

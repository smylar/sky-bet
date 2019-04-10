package com.skyb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyb.entity.GameState;
import com.skyb.entity.GameStatus;
import com.skyb.repository.GameStateRespository;

/**
 * Service for changing game states
 * 
 * @author paul
 *
 */
@Service
public class GameService {
    
    @Autowired
    private GameStateRespository gameStateRepository;
    
    /**
     * Flag that the indicated game cannot accept more bets
     * 
     * @param gameId - The ID of the game to update
     * @return       - The new {@link GameStatus}
     */
    public GameStatus noMoreBets(final long gameId) {
        return updateState(gameId, GameStatus.CLOSED);
    }
    
    /**
     * Flag that the indicated game can now accept bets
     * 
     * @param gameId - The ID of the game to update
     * @return       - The new {@link GameStatus}
     */
    public GameStatus acceptBets(final long gameId) {
        return updateState(gameId, GameStatus.OPEN);
    }
    
    private GameStatus updateState(final long gameId, final GameStatus status) {
        return gameStateRepository.save(GameState.builder()
                                          .id(gameId)
                                          .status(status)
                                          .build())
                                  .getStatus();
    }

}

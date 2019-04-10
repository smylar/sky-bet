package com.skyb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skyb.entity.GameState;

/**
 * Interface to the roulette.games table
 * 
 * @author paul
 *
 */
public interface GameStateRespository extends JpaRepository<GameState, Long> {

}

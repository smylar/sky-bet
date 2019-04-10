package com.skyb.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skyb.entity.BetStatus;
import com.skyb.entity.RouletteBet;

/**
 * Repository for roulette bet information
 * 
 * @author paul
 *
 */
public interface RouletteRepository extends JpaRepository<RouletteBet, Long> {

    public Set<RouletteBet> getByGameIdAndStatus(final long id, BetStatus status);
}

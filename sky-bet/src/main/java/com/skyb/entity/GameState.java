package com.skyb.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity defining a game state
 * 
 * @author paul
 *
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table(schema="roulette", name="games")
public class GameState {
    
    @Id
    private long id;
    
    @Enumerated(EnumType.STRING)
    private GameStatus status;
}

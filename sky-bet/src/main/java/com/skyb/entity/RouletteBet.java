package com.skyb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The roulette bet entity
 * 
 * @author paul
 *
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table(schema="roulette", name="bets")
public class RouletteBet {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column(name="customer_id", nullable = false)
    private Long customerId;
    
    @Column(name="game_id", nullable = false)
    private Long gameId;
    
    @Column(name="bet_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Bet betType;
    
    @Column(name = "number_selection")
    private String numberSelection; //will be a json string of numbers, there are some array options but take to long to sort out
                                    //suppose I could also use a DTO to translate it
    
    @Column(name="bet_value", nullable = false)
    private Double value;
    
    @Column(name="bet_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BetStatus status;
    
    @Column(name="winning_number")
    private Integer winningNumber;
    
    @Column(name="winnings")
    private Double winnings;
}

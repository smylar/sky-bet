package com.skyb.service;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyb.controller.exception.RequestException;
import com.skyb.data.NewBet;
import com.skyb.entity.Bet;
import com.skyb.entity.BetStatus;
import com.skyb.entity.GameState;
import com.skyb.entity.GameStatus;
import com.skyb.entity.RouletteBet;
import com.skyb.repository.GameStateRespository;
import com.skyb.repository.RouletteRepository;

/**
 * Unit tests for the roulette controller,
 * though this kind of tests the {@link Bet} enumeration too which may need to be split out
 * 
 * @author paul
 *
 */
@RunWith(SpringRunner.class)
public class RouletteServiceTests {
    
    @Mock
    private RouletteRepository rouletteRepository;
    
    @Mock
    private GameStateRespository gameStateRepository;
    
    @Mock
    private RandomGenerator random;
    
    @InjectMocks
    private RouletteService rouletteService;
    
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        ReflectionTestUtils.setField(rouletteService, "mapper", new ObjectMapper());
        when(random.generateRandomInt(anyInt(), anyInt())).thenReturn(1);
        when(gameStateRepository.findById(1L)).thenReturn(Optional.of(new GameState(1, GameStatus.OPEN)));
        when(gameStateRepository.findById(2L)).thenReturn(Optional.of(new GameState(1, GameStatus.CLOSED)));
    }
    
    @Test
    public void testPlaceBet() {
        final NewBet bet = new NewBet(1L,2L,Bet.SPLIT,10d, Optional.of(Set.of(1,2)));
        RouletteBet expected = RouletteBet.builder().betType(bet.getBet())
                                                    .customerId(bet.getCustomerId())
                                                    .gameId(bet.getGameId())
                                                    .status(BetStatus.PENDING)
                                                    .value(bet.getValue())
                                                    .numberSelection(Set.of(1,2).toString()).build();
        
        when(rouletteRepository.save(any(RouletteBet.class))).thenReturn(expected);
        rouletteService.placeBet(bet);
        verify(rouletteRepository, times(1)).save(expected);
    }
    
    @Test
    public void testPlaceBetClosed() {
        final NewBet bet = new NewBet(2L,2L,Bet.SPLIT,10d, Optional.of(Set.of(1,2)));

        exception.expect(RequestException.class);
        exception.expectMessage("Could not place bet");
        
        when(rouletteRepository.save(any(RouletteBet.class))).thenReturn(null);
        rouletteService.placeBet(bet);
        verify(rouletteRepository, never()).save(any(RouletteBet.class));      
        
    }
    
    @Test
    public void testPlaceBetIllegal() {
        final NewBet bet = new NewBet(1L,2L,Bet.SPLIT,10d, Optional.of(Set.of(1,2,3)));

        exception.expect(RequestException.class);
        exception.expectMessage("Illegal bet!");
        
        when(rouletteRepository.save(any(RouletteBet.class))).thenReturn(null);
        rouletteService.placeBet(bet);
        verify(rouletteRepository, never()).save(any(RouletteBet.class));      
        
    }
    
    @Test
    public void testResolveWin() {
        RouletteBet pending = RouletteBet.builder().id(100L)
                                                   .betType(Bet.SPLIT)
                                                   .status(BetStatus.PENDING)
                                                   .value(10D)
                                                   .numberSelection(Set.of(1,2).toString()).build();
        
        RouletteBet expected = RouletteBet.builder().id(100L)
                                                    .betType(Bet.SPLIT)
                                                    .status(BetStatus.WON)
                                                    .value(10D)
                                                    .numberSelection(Set.of(1,2).toString())
                                                    .winningNumber(1)
                                                    .winnings(180d).build();
        
        when(rouletteRepository.getByGameIdAndStatus(1L, BetStatus.PENDING)).thenReturn(Set.of(pending));
        
        int winningNumber = rouletteService.resolveRound(1L);
        assertEquals(1, winningNumber);
        verify(rouletteRepository, times(1)).save(expected);
    }
    
    @Test
    public void testResolveLoss() {
        RouletteBet pending = RouletteBet.builder().id(100L)
                                                   .betType(Bet.BLACK)
                                                   .status(BetStatus.PENDING)
                                                   .value(10D)
                                                   .numberSelection("[]").build();
        
        RouletteBet expected = RouletteBet.builder().id(100L)
                                                    .betType(Bet.BLACK)
                                                    .status(BetStatus.LOST)
                                                    .value(10D)
                                                    .winningNumber(1)
                                                    .winnings(0d)
                                                    .numberSelection("[]").build();
        
        when(rouletteRepository.getByGameIdAndStatus(1L, BetStatus.PENDING)).thenReturn(Set.of(pending));
        
        int winningNumber = rouletteService.resolveRound(1L);
        assertEquals(1, winningNumber);
        verify(rouletteRepository, times(1)).save(expected);
    }
    
    @Test
    public void testCancel() {
        RouletteBet existing = RouletteBet.builder().status(BetStatus.PENDING)
                                                   .gameId(1L)
                                                   .customerId(5L)
                                                   .build();
        
        when(rouletteRepository.findById(1L)).thenReturn(Optional.of(existing));
        assertTrue(rouletteService.cancelBet(1L,5L));
        verify(rouletteRepository, times(1)).deleteById(1L);
        
    }
    
    @Test
    public void testCancelWrongState() {
        RouletteBet existing = RouletteBet.builder().status(BetStatus.LOST)
                                                   .gameId(1L)
                                                   .customerId(5L)
                                                   .build();
        
        when(rouletteRepository.findById(1L)).thenReturn(Optional.of(existing));
        assertFalse(rouletteService.cancelBet(1L,5L));
        verify(rouletteRepository, never()).deleteById(anyLong());
        
    }
    
    @Test
    public void testCancelWrongCustomer() {
        RouletteBet existing = RouletteBet.builder().status(BetStatus.PENDING)
                                                   .gameId(1L)
                                                   .customerId(5L)
                                                   .build();
        
        when(rouletteRepository.findById(1L)).thenReturn(Optional.of(existing));
        assertFalse(rouletteService.cancelBet(1L,77L));
        verify(rouletteRepository, never()).deleteById(anyLong());
        
    }
    
    @Test
    public void testCancelClosedGame() {
        RouletteBet existing = RouletteBet.builder().status(BetStatus.PENDING)
                                                   .gameId(2L)
                                                   .customerId(5L)
                                                   .build();
        
        when(rouletteRepository.findById(1L)).thenReturn(Optional.of(existing));
        assertFalse(rouletteService.cancelBet(1L,5L));
        verify(rouletteRepository, never()).deleteById(anyLong());
        
    }
    
    @Test
    public void testGet() {
        RouletteBet existing = RouletteBet.builder().id(77L)
                                                    .customerId(88L).build();
                                                   
        when(rouletteRepository.findById(77L)).thenReturn(Optional.of(existing));
        assertEquals(existing, rouletteService.getBet(77L, 88L));
    }
    
    @Test
    public void testGetWrongCustomer() {
        RouletteBet existing = RouletteBet.builder().id(77L)
                                                    .customerId(88L).build();
                                                   
        when(rouletteRepository.findById(77L)).thenReturn(Optional.of(existing));
        
        exception.expect(RequestException.class);
        exception.expectMessage("Bet not found");
        rouletteService.getBet(77L, 99L);
    }
    
    @Test
    public void testGetNoRecord() {                                                   
        when(rouletteRepository.findById(77L)).thenReturn(Optional.empty());
        
        exception.expect(RequestException.class);
        exception.expectMessage("Bet not found");
        rouletteService.getBet(77L, 99L);
    }

}

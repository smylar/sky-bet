package com.skyb.controller;

import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.skyb.controller.exception.RequestException;
import com.skyb.entity.GameStatus;
import com.skyb.service.GameService;
import com.skyb.service.RouletteService;

import lombok.extern.slf4j.Slf4j;

/**
 * REST endpoints for handling interactions with the roulette game
 * 
 * @author paul
 *
 */
@RestController
@Slf4j
@RequestMapping("/control/roulette")
public class RouletteAdminController {
    
    @Autowired
    private RouletteService rouletteService;
    
    @Autowired
    private GameService gameService;
    
    /**
     * Create a new roulette game
     * 
     * @param request
     * @return The number generated for the roulette result
     */
    @PutMapping(value="/newgame")
    @ResponseBody
    public void newgame(final HttpServletRequest request) {
        throw new RequestException("Not implemented");
        //TODO
    }
    
    /**
     * Prevents more bets being made
     * 
     * @param request
     * @return The number generated for the roulette result
     */
    @PutMapping(value="/nomorebets/{gameId}")
    @ResponseBody
    public GameStatus noMoreBets(final HttpServletRequest request, @PathVariable long gameId) {
        return tryCatch(request, () -> gameService.noMoreBets(gameId));
    }
    
    /**
     * Allow bets to be made
     * 
     * @param request
     * @return The number generated for the roulette result
     */
    @PutMapping(value="/acceptbets/{gameId}")
    @ResponseBody
    public GameStatus acceptBets(final HttpServletRequest request, @PathVariable long gameId) {
        return tryCatch(request, () -> gameService.acceptBets(gameId));
    }
    
    /**
     * End point resolve a game run.
     * 
     * Generates a number for the ball to land on and resolves pending bets
     * 
     * @param request
     * @return The number generated for the roulette result
     */
    @PutMapping(value="/resolve/{gameId}")
    @ResponseBody
    public Integer resolve(final HttpServletRequest request, @PathVariable long gameId) {
        //should at least add basic auth to this, should not be a customer facing endpoint - maybe move to another controller too
        return tryCatch(request, () -> rouletteService.resolveRound(gameId));
    }
    
    private <R> R tryCatch(HttpServletRequest request, Supplier<R> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            log.error("Error on {}", request.getServletPath(), e);
            throw new RequestException(e);
        }
    }

}

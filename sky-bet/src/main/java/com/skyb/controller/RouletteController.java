package com.skyb.controller;

import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.skyb.controller.exception.RequestException;
import com.skyb.data.NewBet;
import com.skyb.data.ThrowingSupplier;
import com.skyb.entity.RouletteBet;
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
@RequestMapping("/public/roulette")
public class RouletteController {
    
    @Autowired
    private RouletteService rouletteService;
    
    /**
     * End point places a new bet
     * 
     * @param request
     * @return {@link RouletteBet}
     */
    @PostMapping(value="/placebet")
    @ResponseBody
    public RouletteBet placeBet(final HttpServletRequest request, @RequestBody NewBet bet) {
        return tryCatch(request, () -> rouletteService.placeBet(bet)).get();
    }
    
    
    /**
     * End point that changes an existing bet
     * 
     * @param request
     * @return {@link RouletteBet}
     */
    @PutMapping(value="/cancelbet/{betId}/{custId}")
    @ResponseBody
    public Boolean cancelBet(final HttpServletRequest request, @PathVariable("betId") long betId, @PathVariable("custId") long custId) {
      //obviously would need mechanisms to ensure it is the same user who placed the bet e.g. use oauth
        return tryCatch(request, () -> rouletteService.cancelBet(betId, custId)).get();
    }
    
    /**
     * End point that changes an existing bet
     * 
     * @param request
     * @return {@link RouletteBet}
     */
    @PostMapping(value="/changebet/{id}")
    @ResponseBody
    public RouletteBet changeBet(final HttpServletRequest request, @PathVariable long id, @RequestBody NewBet bet) {
      //obviously would need mechanisms to ensure it is the same user who placed the bet e.g. use oauth
        throw new RequestException("Not implemented");
        //TODO - sorry didn't get round to this, but likely to be necessary
    }
    
    /**
     * End point that retrieves a bet
     * 
     * @param request
     * @return {@link RouletteBet}
     */
    @GetMapping(value="/getbet/{betId}/{custId}")
    @ResponseBody
    public RouletteBet getBet(final HttpServletRequest request, @PathVariable("betId") long betId, @PathVariable("custId") long custId) {
        //obviously would need mechanisms to ensure it is the same user who placed the bet e.g. use oauth
        return tryCatch(request, () -> rouletteService.getBet(betId, custId)).get();
    }
    
//    private <R> R tryCatch(HttpServletRequest request, Supplier<R> supplier) {
//        try {
//            return supplier.get();
//        } catch (Exception e) {
//            log.error("Error on {}", request.getServletPath(), e);
//            throw new RequestException(e);
//        }
//    }
    
    private <R> Supplier<R> tryCatch(HttpServletRequest request, ThrowingSupplier<R, Exception> supplier) {
        
        return () -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                log.error("Error on {}", request.getServletPath(), e);
                throw new RequestException(e);
            }
        };
    }

}

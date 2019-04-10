package com.skyb.controller;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.skyb.Application;
import com.skyb.configuration.TestConfiguration;
import com.skyb.entity.Bet;
import com.skyb.entity.BetStatus;
import com.skyb.entity.RouletteBet;
import com.skyb.repository.RouletteRepository;

/**
 * Integration tests for the "control" (admin) controller
 * 
 * @author paul
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, 
               classes = {Application.class, TestConfiguration.class})
@TestPropertySource("classpath:application-test.properties")
@DirtiesContext
public class RouletteControllerAdminTests {

private static final String BASE_URL = "http://localhost:%s/%s/control/roulette/%s";
    
    private HttpClient http = HttpClients.createDefault();
    
    @Value("${server.port}")
    private String port;
    
    @Value("${server.servlet.context-path}")
    private String context;
    
    @Autowired
    private RouletteRepository rouletteRepository;
    
    @Before
    public void setup() {
        rouletteRepository.deleteAll();
    }
    
    @Test
    public void testResolveWin() throws UnsupportedOperationException, UnsupportedEncodingException, IOException {
        RouletteBet repBet = rouletteRepository.save(RouletteBet.builder().betType(Bet.SPLIT)
                                                                          .customerId(22L)
                                                                          .gameId(1L)
                                                                          .status(BetStatus.PENDING)
                                                                          .value(10d)
                                                                          .numberSelection(Set.of(1,2).toString()).build());
        
        Pair<String, StatusLine> response = getResponse(getPut("resolve/1"));
        assertEquals(200, response.getSecond().getStatusCode());
        assertEquals("1", response.getFirst());
        
        RouletteBet updated = rouletteRepository.findById(repBet.getId()).get();
        assertEquals(BetStatus.WON, updated.getStatus());
        assertEquals(1, updated.getWinningNumber(), 0);
        assertEquals(180, updated.getWinnings(), 0);
    }
    
    
    @Test
    public void testResolveLose() throws UnsupportedOperationException, UnsupportedEncodingException, IOException {
        RouletteBet repBet = rouletteRepository.save(RouletteBet.builder().betType(Bet.BLACK)
                                                                          .customerId(22L)
                                                                          .gameId(1L)
                                                                          .status(BetStatus.PENDING)
                                                                          .value(10d)
                                                                          .numberSelection(Set.of().toString()).build());
        
        Pair<String, StatusLine> response = getResponse(getPut("resolve/1"));
        assertEquals(200, response.getSecond().getStatusCode());
        assertEquals("1", response.getFirst());
        
        RouletteBet updated = rouletteRepository.findById(repBet.getId()).get();
        assertEquals(BetStatus.LOST, updated.getStatus());
        assertEquals(1, updated.getWinningNumber(), 0);
        assertEquals(0, updated.getWinnings(), 0);
    }
    
    @Test
    public void testResolveUnchangedIfNotPending() throws UnsupportedOperationException, UnsupportedEncodingException, IOException {
        RouletteBet repBet = rouletteRepository.save(RouletteBet.builder().betType(Bet.SPLIT)
                                                                          .customerId(22L)
                                                                          .gameId(1L)
                                                                          .status(BetStatus.LOST)
                                                                          .value(10d)
                                                                          .numberSelection(Set.of(1,2).toString()).build());
        
        Pair<String, StatusLine> response = getResponse(getPut("resolve/1"));
        assertEquals(200, response.getSecond().getStatusCode());
        assertEquals("1", response.getFirst());
        
        RouletteBet updated = rouletteRepository.findById(repBet.getId()).get();
        assertEquals(repBet, updated);
    }
    
    
    private String getUrl(String path) {
        return String.format(BASE_URL, port, context, path);
    }
    
    private HttpRequestBase getPut(String path) throws UnsupportedEncodingException {
        return new HttpPut(getUrl(path));
    }
    
    private Pair<String,StatusLine> getResponse(HttpRequestBase request) throws UnsupportedOperationException, IOException {
        String auth = "test:test";
        byte[] encodedAuth = Base64.encodeBase64(
          auth.getBytes(StandardCharsets.ISO_8859_1));
        
        String authHeader = "Basic " + new String(encodedAuth);
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        
        HttpResponse response = http.execute(request);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
            return Pair.of(reader.readLine(), response.getStatusLine());
       }
    }
}

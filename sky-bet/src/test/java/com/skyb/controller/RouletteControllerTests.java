package com.skyb.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
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

import com.google.gson.Gson;
import com.skyb.Application;
import com.skyb.configuration.TestConfiguration;
import com.skyb.entity.Bet;
import com.skyb.entity.BetStatus;
import com.skyb.entity.RouletteBet;
import com.skyb.repository.RouletteRepository;

/**
 * Integration tests for the roulette controller
 * 
 * @author paul
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, 
               classes = {Application.class, TestConfiguration.class})
@TestPropertySource("classpath:application-test.properties")
@DirtiesContext
public class RouletteControllerTests {

private static final String BASE_URL = "http://localhost:%s/%s/public/roulette/%s";
    
    private HttpClient http = HttpClients.createDefault();
    
    private Gson gson = new Gson();
    
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
    public void testPlaceBet() throws ClientProtocolException, IOException {
        List<RouletteBet> repBet = rouletteRepository.findAll(); 
        assertTrue(repBet.isEmpty());
        
        String payload = "{\"customerId\": 2,\"gameId\": 1,\"bet\": \"SPLIT\",\"value\": 10,\"numbers\": [1,2]}";
        Pair<RouletteBet, StatusLine> response = getResponse(getPost("placebet", payload), RouletteBet.class);
        repBet = rouletteRepository.findAll(); 
        RouletteBet bet = response.getFirst();
        assertEquals(200, response.getSecond().getStatusCode());
        assertEquals(1, repBet.size());
        assertEquals(bet, repBet.get(0));
        assertEquals(2L, bet.getCustomerId(), 0);
        assertEquals(1L, bet.getGameId(), 0);
        assertEquals(Bet.SPLIT, bet.getBetType());
        assertEquals("[1, 2]", bet.getNumberSelection());
        assertEquals(BetStatus.PENDING, bet.getStatus());
        assertEquals(10, bet.getValue(), 0);
        assertNull(bet.getWinningNumber());
        assertNull(bet.getWinnings());
    }
    
    @Test
    public void testPlaceBetIllegal() throws ClientProtocolException, IOException {
        List<RouletteBet> repBet = rouletteRepository.findAll(); 
        assertTrue(repBet.isEmpty());
        
        String payload = "{\"customerId\": 2,\"gameId\": 1,\"bet\": \"STRAIGHTUP\",\"value\": 10,\"numbers\": [1,2]}";
        Pair<RouletteBet, StatusLine> response = getResponse(getPost("placebet", payload), RouletteBet.class);
        repBet = rouletteRepository.findAll(); 
        RouletteBet bet = response.getFirst();
        assertTrue(repBet.isEmpty());
        assertEquals(400, response.getSecond().getStatusCode());
        assertNull(bet.getId());
    }
    
    @Test
    public void testGetBet() throws ClientProtocolException, IOException {
        
        RouletteBet repBet = rouletteRepository.save(RouletteBet.builder().betType(Bet.SPLIT)
                                                                          .customerId(22L)
                                                                          .gameId(1L)
                                                                          .status(BetStatus.PENDING)
                                                                          .value(10d)
                                                                          .numberSelection(Set.of(1,2).toString()).build());
        
        Pair<RouletteBet, StatusLine> response = getResponse(getGet("getbet/"+repBet.getId()+"/22"), RouletteBet.class);
        RouletteBet bet = response.getFirst();
        assertEquals(200, response.getSecond().getStatusCode());
        assertEquals(bet, repBet);
        assertEquals(22L, bet.getCustomerId(), 0);
        assertEquals(1L, bet.getGameId(), 0);
        assertEquals(Bet.SPLIT, bet.getBetType());
        assertEquals(BetStatus.PENDING, bet.getStatus());
        assertEquals(10, bet.getValue(), 0);
        assertNull(bet.getWinningNumber());
        assertNull(bet.getWinnings());
    }
    
    @Test
    public void testGetBetNotFound() throws ClientProtocolException, IOException {        

        Pair<RouletteBet, StatusLine> response = getResponse(getGet("getbet/1/22"), RouletteBet.class);
        RouletteBet bet = response.getFirst();
        assertEquals(400, response.getSecond().getStatusCode());
        assertNull(bet.getId());
    }
    
    private String getUrl(String path) {
        return String.format(BASE_URL, port, context, path);
    }
    
    private HttpRequestBase getPost(String path, String jsonPayload) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(getUrl(path));
        httpPost.setEntity(new StringEntity(jsonPayload));
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        return httpPost;
    }
    
    private HttpRequestBase getGet(String path) throws UnsupportedEncodingException {
        return new HttpGet(getUrl(path));
    }
    
    private <T> Pair<T,StatusLine> getResponse(HttpRequestBase request, Class<T> outputType) throws UnsupportedOperationException, IOException {
        HttpResponse response = http.execute(request);
        try (Reader reader = new InputStreamReader(response.getEntity().getContent())) {
            return Pair.of(gson.fromJson(reader, outputType), response.getStatusLine());
       }
    }
}

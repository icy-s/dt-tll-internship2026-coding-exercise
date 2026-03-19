package com.dynatrace.pong.controller;

import com.dynatrace.pong.dto.MatchRequest;
import com.dynatrace.pong.dto.MatchResponse;
import com.dynatrace.pong.dto.StandingResponse;
import com.dynatrace.pong.exception.InvalidMatchResultException;
import com.dynatrace.pong.service.TournamentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TournamentController.class)
class TournamentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TournamentService tournamentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void recordMatch_validInput_shouldReturn201() throws Exception {
        MatchRequest request = new MatchRequest(1L, 2L, 11, 8);
        MatchResponse response = new MatchResponse(5L, 1L, 2L, 11, 8, 1L,
                LocalDateTime.parse("2026-03-19T12:00:00"));

        when(tournamentService.recordMatch(any(MatchRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.winnerId").value(1))
                .andExpect(jsonPath("$.playerOneScore").value(11))
                .andExpect(jsonPath("$.playerTwoScore").value(8));
    }

    @Test
    void recordMatch_negativeScore_shouldReturn400() throws Exception {
        MatchRequest request = new MatchRequest(1L, 2L, -1, 11);

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("playerOneScore")));
    }

    @Test
    void recordMatch_invalidResult_shouldReturn400() throws Exception {
        MatchRequest request = new MatchRequest(1L, 2L, 11, 10);
        when(tournamentService.recordMatch(any(MatchRequest.class)))
                .thenThrow(new InvalidMatchResultException("The winner must lead by at least 2 points"));

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("at least 2")));
    }

    @Test
    void getStandings_shouldReturnOrderedTable() throws Exception {
        List<StandingResponse> standings = List.of(
                new StandingResponse(1L, "Timo", "Boll", 2, 2, 0, 22, 14, 8, 6),
                new StandingResponse(2L, "Ma", "Long", 2, 1, 1, 20, 18, 2, 3)
        );
        when(tournamentService.getStandings()).thenReturn(standings);

        mockMvc.perform(get("/api/standings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].playerId").value(1))
                .andExpect(jsonPath("$[0].tournamentPoints").value(6))
                .andExpect(jsonPath("$[1].playerId").value(2));
    }
}

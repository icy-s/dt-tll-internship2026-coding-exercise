package com.dynatrace.pong.controller;

import com.dynatrace.pong.dto.PlayerRequest;
import com.dynatrace.pong.dto.PlayerResponse;
import com.dynatrace.pong.exception.DuplicateEmailException;
import com.dynatrace.pong.exception.GlobalExceptionHandler;
import com.dynatrace.pong.exception.PlayerHasMatchesException;
import com.dynatrace.pong.exception.PlayerNotFoundException;
import com.dynatrace.pong.service.PlayerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlayerController.class)
class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlayerService playerService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private PlayerResponse sampleResponse() {
        return new PlayerResponse(1L, "Roger", "Federer", "roger@tennis.com", "Switzerland", 3);
    }

    // --- POST /api/players ---

    @Test
    void createPlayer_validInput_shouldReturn201() throws Exception {
        PlayerRequest request = new PlayerRequest("Roger", "Federer", "roger@tennis.com", "Switzerland", 3);
        when(playerService.createPlayer(any(PlayerRequest.class))).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Roger"))
                .andExpect(jsonPath("$.lastName").value("Federer"))
                .andExpect(jsonPath("$.email").value("roger@tennis.com"))
                .andExpect(jsonPath("$.country").value("Switzerland"))
                .andExpect(jsonPath("$.ranking").value(3));
    }

    @Test
    void createPlayer_blankFirstName_shouldReturn400() throws Exception {
        PlayerRequest request = new PlayerRequest("", "Federer", "roger@tennis.com", "Switzerland", 3);

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void createPlayer_blankLastName_shouldReturn400() throws Exception {
        PlayerRequest request = new PlayerRequest("Roger", "", "roger@tennis.com", "Switzerland", 3);

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPlayer_invalidEmail_shouldReturn400() throws Exception {
        PlayerRequest request = new PlayerRequest("Roger", "Federer", "not-an-email", "Switzerland", 3);

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("email")));
    }

    @Test
    void createPlayer_blankEmail_shouldReturn400() throws Exception {
        PlayerRequest request = new PlayerRequest("Roger", "Federer", "", "Switzerland", 3);

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPlayer_rankingTooLow_shouldReturn400() throws Exception {
        PlayerRequest request = new PlayerRequest("Roger", "Federer", "roger@tennis.com", "Switzerland", 0);

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("ranking")));
    }

    @Test
    void createPlayer_rankingTooHigh_shouldReturn400() throws Exception {
        PlayerRequest request = new PlayerRequest("Roger", "Federer", "roger@tennis.com", "Switzerland", 10001);

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPlayer_duplicateEmail_shouldReturn409() throws Exception {
        PlayerRequest request = new PlayerRequest("Roger", "Federer", "roger@tennis.com", "Switzerland", 3);
        when(playerService.createPlayer(any(PlayerRequest.class)))
                .thenThrow(new DuplicateEmailException("roger@tennis.com"));

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("roger@tennis.com")));
    }

    @Test
    void createPlayer_nullRanking_shouldReturn201() throws Exception {
        PlayerRequest request = new PlayerRequest("Roger", "Federer", "roger@tennis.com", "Switzerland", null);
        when(playerService.createPlayer(any(PlayerRequest.class))).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    // --- GET /api/players/{id} ---

    @Test
    void getPlayerById_found_shouldReturn200() throws Exception {
        when(playerService.getPlayerById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/players/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Roger"));
    }

    @Test
    void getPlayerById_notFound_shouldReturn404() throws Exception {
        when(playerService.getPlayerById(99L)).thenThrow(new PlayerNotFoundException(99L));

        mockMvc.perform(get("/api/players/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("99")));
    }

    // --- GET /api/players ---

    @Test
    void getAllPlayers_shouldReturnList() throws Exception {
        PlayerResponse p1 = sampleResponse();
        PlayerResponse p2 = new PlayerResponse(2L, "Rafael", "Nadal", "rafa@tennis.com", "Spain", 2);
        when(playerService.getAllPlayers()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("Roger"))
                .andExpect(jsonPath("$[1].firstName").value("Rafael"));
    }

    @Test
    void getAllPlayers_empty_shouldReturnEmptyList() throws Exception {
        when(playerService.getAllPlayers()).thenReturn(List.of());

        mockMvc.perform(get("/api/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // --- DELETE /api/players/{id} ---

    @Test
    void deletePlayer_existing_shouldReturn204() throws Exception {
        doNothing().when(playerService).deletePlayer(1L);

        mockMvc.perform(delete("/api/players/1"))
                .andExpect(status().isNoContent());

        verify(playerService).deletePlayer(1L);
    }

    @Test
    void deletePlayer_notFound_shouldReturn404() throws Exception {
        doThrow(new PlayerNotFoundException(99L)).when(playerService).deletePlayer(99L);

        mockMvc.perform(delete("/api/players/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("99")));
    }

    @Test
    void deletePlayer_withRecordedMatches_shouldReturn409() throws Exception {
        doThrow(new PlayerHasMatchesException(1L)).when(playerService).deletePlayer(1L);

        mockMvc.perform(delete("/api/players/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("cannot be deleted")));
    }
}


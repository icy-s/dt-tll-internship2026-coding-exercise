package com.dynatrace.pong.service;

import com.dynatrace.pong.dto.PlayerRequest;
import com.dynatrace.pong.dto.PlayerResponse;
import com.dynatrace.pong.exception.DuplicateEmailException;
import com.dynatrace.pong.exception.PlayerHasMatchesException;
import com.dynatrace.pong.exception.PlayerNotFoundException;
import com.dynatrace.pong.model.Player;
import com.dynatrace.pong.repository.PlayerRepository;
import com.dynatrace.pong.repository.TournamentMatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TournamentMatchRepository tournamentMatchRepository;

    @InjectMocks
    private PlayerService playerService;

    private PlayerRequest validRequest;
    private Player savedPlayer;

    @BeforeEach
    void setUp() {
        validRequest = new PlayerRequest("Roger", "Federer", "roger@tennis.com", "Switzerland", 3);

        savedPlayer = new Player("Roger", "Federer", "roger@tennis.com", "Switzerland", 3);
        savedPlayer.setId(1L);
    }

    @Test
    void createPlayer_shouldReturnPlayerResponse() {
        when(playerRepository.existsByEmail("roger@tennis.com")).thenReturn(false);
        when(playerRepository.save(any(Player.class))).thenReturn(savedPlayer);

        PlayerResponse response = playerService.createPlayer(validRequest);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("Roger");
        assertThat(response.getLastName()).isEqualTo("Federer");
        assertThat(response.getEmail()).isEqualTo("roger@tennis.com");
        assertThat(response.getCountry()).isEqualTo("Switzerland");
        assertThat(response.getRanking()).isEqualTo(3);
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void createPlayer_withDuplicateEmail_shouldThrowException() {
        when(playerRepository.existsByEmail("roger@tennis.com")).thenReturn(true);

        assertThatThrownBy(() -> playerService.createPlayer(validRequest))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("roger@tennis.com");

        verify(playerRepository, never()).save(any());
    }

    @Test
    void getPlayerById_shouldReturnPlayer() {
        when(playerRepository.findById(1L)).thenReturn(Optional.of(savedPlayer));

        PlayerResponse response = playerService.getPlayerById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("Roger");
    }

    @Test
    void getPlayerById_notFound_shouldThrowException() {
        when(playerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerService.getPlayerById(99L))
                .isInstanceOf(PlayerNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getAllPlayers_shouldReturnList() {
        Player player2 = new Player("Rafael", "Nadal", "rafa@tennis.com", "Spain", 2);
        player2.setId(2L);

        when(playerRepository.findAll()).thenReturn(List.of(savedPlayer, player2));

        List<PlayerResponse> players = playerService.getAllPlayers();

        assertThat(players).hasSize(2);
        assertThat(players.get(0).getFirstName()).isEqualTo("Roger");
        assertThat(players.get(1).getFirstName()).isEqualTo("Rafael");
    }

    @Test
    void getAllPlayers_empty_shouldReturnEmptyList() {
        when(playerRepository.findAll()).thenReturn(List.of());

        List<PlayerResponse> players = playerService.getAllPlayers();

        assertThat(players).isEmpty();
    }

    @Test
    void deletePlayer_shouldDeleteSuccessfully() {
        when(playerRepository.existsById(1L)).thenReturn(true);
        when(tournamentMatchRepository.existsByPlayerOneIdOrPlayerTwoId(1L, 1L)).thenReturn(false);

        playerService.deletePlayer(1L);

        verify(playerRepository).deleteById(1L);
    }

    @Test
    void deletePlayer_notFound_shouldThrowException() {
        when(playerRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> playerService.deletePlayer(99L))
                .isInstanceOf(PlayerNotFoundException.class)
                .hasMessageContaining("99");

        verify(playerRepository, never()).deleteById(any());
    }

    @Test
    void deletePlayer_withRecordedMatches_shouldThrowException() {
        when(playerRepository.existsById(1L)).thenReturn(true);
        when(tournamentMatchRepository.existsByPlayerOneIdOrPlayerTwoId(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> playerService.deletePlayer(1L))
                .isInstanceOf(PlayerHasMatchesException.class)
                .hasMessageContaining("1");

        verify(playerRepository, never()).deleteById(any());
    }
}


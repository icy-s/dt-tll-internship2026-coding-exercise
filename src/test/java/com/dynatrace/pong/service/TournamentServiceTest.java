package com.dynatrace.pong.service;

import com.dynatrace.pong.dto.MatchRequest;
import com.dynatrace.pong.dto.MatchResponse;
import com.dynatrace.pong.dto.StandingResponse;
import com.dynatrace.pong.exception.InvalidMatchResultException;
import com.dynatrace.pong.exception.PlayerNotFoundException;
import com.dynatrace.pong.model.Player;
import com.dynatrace.pong.model.TournamentMatch;
import com.dynatrace.pong.repository.PlayerRepository;
import com.dynatrace.pong.repository.TournamentMatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TournamentServiceTest {

    @Mock
    private TournamentMatchRepository tournamentMatchRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private TournamentService tournamentService;

    private Player playerOne;
    private Player playerTwo;
    private Player playerThree;
    private Player playerFour;

    @BeforeEach
    void setUp() {
        playerOne = new Player("Timo", "Boll", "timo@pong.com", "Germany", 1);
        playerOne.setId(1L);

        playerTwo = new Player("Ma", "Long", "ma@pong.com", "China", 2);
        playerTwo.setId(2L);

        playerThree = new Player("Jan-Ove", "Waldner", "jano@pong.com", "Sweden", 3);
        playerThree.setId(3L);

        playerFour = new Player("Mima", "Ito", "mima@pong.com", "Japan", 4);
        playerFour.setId(4L);
    }

    @Test
    void recordMatch_shouldPersistValidResult() {
        when(playerRepository.findById(1L)).thenReturn(Optional.of(playerOne));
        when(playerRepository.findById(2L)).thenReturn(Optional.of(playerTwo));
        when(tournamentMatchRepository.save(any(TournamentMatch.class))).thenAnswer(invocation -> {
            TournamentMatch match = invocation.getArgument(0);
            match.setId(10L);
            return match;
        });

        MatchResponse response = tournamentService.recordMatch(new MatchRequest(1L, 2L, 11, 7));

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getPlayerOneId()).isEqualTo(1L);
        assertThat(response.getPlayerTwoId()).isEqualTo(2L);
        assertThat(response.getPlayerOneScore()).isEqualTo(11);
        assertThat(response.getPlayerTwoScore()).isEqualTo(7);
        assertThat(response.getWinnerId()).isEqualTo(1L);
        assertThat(response.getPlayedAt()).isNotNull();
        verify(tournamentMatchRepository).save(any(TournamentMatch.class));
    }

    @Test
    void recordMatch_shouldAllowExtendedGameWhenWinnerLeadsByTwo() {
        when(playerRepository.findById(1L)).thenReturn(Optional.of(playerOne));
        when(playerRepository.findById(2L)).thenReturn(Optional.of(playerTwo));
        when(tournamentMatchRepository.save(any(TournamentMatch.class))).thenAnswer(invocation -> {
            TournamentMatch match = invocation.getArgument(0);
            match.setId(11L);
            return match;
        });

        MatchResponse response = tournamentService.recordMatch(new MatchRequest(1L, 2L, 12, 14));

        assertThat(response.getWinnerId()).isEqualTo(2L);
        assertThat(response.getPlayerOneScore()).isEqualTo(12);
        assertThat(response.getPlayerTwoScore()).isEqualTo(14);
    }

    @Test
    void recordMatch_withSamePlayer_shouldThrowException() {
        assertThatThrownBy(() -> tournamentService.recordMatch(new MatchRequest(1L, 1L, 11, 3)))
                .isInstanceOf(InvalidMatchResultException.class)
                .hasMessageContaining("different players");

        verifyNoInteractions(playerRepository, tournamentMatchRepository);
    }

    @Test
    void recordMatch_withoutWinnerReachingEleven_shouldThrowException() {
        assertThatThrownBy(() -> tournamentService.recordMatch(new MatchRequest(1L, 2L, 10, 8)))
                .isInstanceOf(InvalidMatchResultException.class)
                .hasMessageContaining("at least 11");

        verifyNoInteractions(playerRepository, tournamentMatchRepository);
    }

    @Test
    void recordMatch_withoutTwoPointLead_shouldThrowException() {
        assertThatThrownBy(() -> tournamentService.recordMatch(new MatchRequest(1L, 2L, 11, 10)))
                .isInstanceOf(InvalidMatchResultException.class)
                .hasMessageContaining("at least 2");

        verifyNoInteractions(playerRepository, tournamentMatchRepository);
    }

    @Test
    void recordMatch_withUnknownPlayer_shouldThrowException() {
        when(playerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tournamentService.recordMatch(new MatchRequest(1L, 2L, 11, 7)))
                .isInstanceOf(PlayerNotFoundException.class)
                .hasMessageContaining("1");

        verify(tournamentMatchRepository, never()).save(any(TournamentMatch.class));
    }

    @Test
    void getStandings_shouldAggregateAndSortTournamentTable() {
        TournamentMatch matchOne = new TournamentMatch(playerOne, playerTwo, 11, 7, playerOne,
                LocalDateTime.parse("2026-03-19T10:00:00"));
        TournamentMatch matchTwo = new TournamentMatch(playerTwo, playerThree, 12, 10, playerTwo,
                LocalDateTime.parse("2026-03-19T11:00:00"));
        TournamentMatch matchThree = new TournamentMatch(playerOne, playerThree, 9, 11, playerThree,
                LocalDateTime.parse("2026-03-19T12:00:00"));

        when(playerRepository.findAll()).thenReturn(List.of(playerOne, playerTwo, playerThree, playerFour));
        when(tournamentMatchRepository.findAll()).thenReturn(List.of(matchOne, matchTwo, matchThree));

        List<StandingResponse> standings = tournamentService.getStandings();

        assertThat(standings).hasSize(4);
        assertThat(standings.get(0).getPlayerId()).isEqualTo(1L);
        assertThat(standings.get(0).getTournamentPoints()).isEqualTo(3);
        assertThat(standings.get(0).getScoreDifference()).isEqualTo(2);

        assertThat(standings.get(1).getPlayerId()).isEqualTo(3L);
        assertThat(standings.get(1).getScoreDifference()).isEqualTo(0);

        assertThat(standings.get(2).getPlayerId()).isEqualTo(2L);
        assertThat(standings.get(2).getScoreDifference()).isEqualTo(-2);

        assertThat(standings.get(3).getPlayerId()).isEqualTo(4L);
        assertThat(standings.get(3).getMatchesPlayed()).isEqualTo(0);
        assertThat(standings.get(3).getTournamentPoints()).isEqualTo(0);
    }
}

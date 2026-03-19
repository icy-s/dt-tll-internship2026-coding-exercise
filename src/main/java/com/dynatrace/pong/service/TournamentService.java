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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class TournamentService {

    private static final int TOURNAMENT_POINTS_FOR_WIN = 3;
    private static final Comparator<StandingResponse> STANDINGS_ORDER =
            Comparator.comparingInt(StandingResponse::getTournamentPoints).reversed()
                    .thenComparing(Comparator.comparingInt(StandingResponse::getScoreDifference).reversed())
                    .thenComparing(Comparator.comparingInt(StandingResponse::getPointsFor).reversed())
                    .thenComparing(Comparator.comparingInt(StandingResponse::getWins).reversed())
                    .thenComparing(StandingResponse::getLastName)
                    .thenComparing(StandingResponse::getFirstName)
                    .thenComparingLong(StandingResponse::getPlayerId);

    private final TournamentMatchRepository tournamentMatchRepository;
    private final PlayerRepository playerRepository;

    public TournamentService(TournamentMatchRepository tournamentMatchRepository, PlayerRepository playerRepository) {
        this.tournamentMatchRepository = tournamentMatchRepository;
        this.playerRepository = playerRepository;
    }

    public MatchResponse recordMatch(MatchRequest request) {
        validatePlayersAreDifferent(request.getPlayerOneId(), request.getPlayerTwoId());
        validateScores(request.getPlayerOneScore(), request.getPlayerTwoScore());

        Player playerOne = findPlayer(request.getPlayerOneId());
        Player playerTwo = findPlayer(request.getPlayerTwoId());
        Player winner = request.getPlayerOneScore() > request.getPlayerTwoScore() ? playerOne : playerTwo;

        TournamentMatch match = new TournamentMatch(
                playerOne,
                playerTwo,
                request.getPlayerOneScore(),
                request.getPlayerTwoScore(),
                winner,
                LocalDateTime.now()
        );

        TournamentMatch savedMatch = tournamentMatchRepository.save(match);
        return toMatchResponse(savedMatch);
    }

    @Transactional(readOnly = true)
    public List<StandingResponse> getStandings() {
        Map<Long, StandingAccumulator> standings = playerRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Player::getId,
                        StandingAccumulator::new,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        for (TournamentMatch match : tournamentMatchRepository.findAll()) {
            StandingAccumulator playerOneStanding = standings.get(match.getPlayerOne().getId());
            StandingAccumulator playerTwoStanding = standings.get(match.getPlayerTwo().getId());
            boolean playerOneWon = match.getWinner().getId().equals(match.getPlayerOne().getId());

            playerOneStanding.recordMatch(match.getPlayerOneScore(), match.getPlayerTwoScore(), playerOneWon);
            playerTwoStanding.recordMatch(match.getPlayerTwoScore(), match.getPlayerOneScore(), !playerOneWon);
        }

        return standings.values().stream()
                .map(StandingAccumulator::toResponse)
                .sorted(STANDINGS_ORDER)
                .toList();
    }

    private Player findPlayer(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id));
    }

    private void validatePlayersAreDifferent(Long playerOneId, Long playerTwoId) {
        if (playerOneId.equals(playerTwoId)) {
            throw new InvalidMatchResultException("A match must be played between two different players");
        }
    }

    private void validateScores(Integer playerOneScore, Integer playerTwoScore) {
        if (playerOneScore.equals(playerTwoScore)) {
            throw new InvalidMatchResultException("Ping pong matches cannot end in a tie");
        }

        int winnerScore = Math.max(playerOneScore, playerTwoScore);
        int loserScore = Math.min(playerOneScore, playerTwoScore);

        if (winnerScore < 11) {
            throw new InvalidMatchResultException("The winner must score at least 11 points");
        }

        if (winnerScore - loserScore < 2) {
            throw new InvalidMatchResultException("The winner must lead by at least 2 points");
        }
    }

    private MatchResponse toMatchResponse(TournamentMatch match) {
        return new MatchResponse(
                match.getId(),
                match.getPlayerOne().getId(),
                match.getPlayerTwo().getId(),
                match.getPlayerOneScore(),
                match.getPlayerTwoScore(),
                match.getWinner().getId(),
                match.getPlayedAt()
        );
    }

    private static class StandingAccumulator {

        private final Long playerId;
        private final String firstName;
        private final String lastName;
        private int matchesPlayed;
        private int wins;
        private int losses;
        private int pointsFor;
        private int pointsAgainst;
        private int tournamentPoints;

        private StandingAccumulator(Player player) {
            this.playerId = player.getId();
            this.firstName = player.getFirstName();
            this.lastName = player.getLastName();
        }

        private void recordMatch(int scoredPoints, int concededPoints, boolean won) {
            matchesPlayed++;
            pointsFor += scoredPoints;
            pointsAgainst += concededPoints;

            if (won) {
                wins++;
                tournamentPoints += TOURNAMENT_POINTS_FOR_WIN;
            } else {
                losses++;
            }
        }

        private StandingResponse toResponse() {
            return new StandingResponse(
                    playerId,
                    firstName,
                    lastName,
                    matchesPlayed,
                    wins,
                    losses,
                    pointsFor,
                    pointsAgainst,
                    pointsFor - pointsAgainst,
                    tournamentPoints
            );
        }
    }
}

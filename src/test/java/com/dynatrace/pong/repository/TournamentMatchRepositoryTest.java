package com.dynatrace.pong.repository;

import com.dynatrace.pong.model.Player;
import com.dynatrace.pong.model.TournamentMatch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TournamentMatchRepositoryTest {

    @Autowired
    private TournamentMatchRepository tournamentMatchRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    void existsByPlayerOneIdOrPlayerTwoId_shouldDetectRecordedMatches() {
        Player playerOne = playerRepository.save(new Player("Timo", "Boll", "timo@pong.com", "Germany", 1));
        Player playerTwo = playerRepository.save(new Player("Ma", "Long", "ma@pong.com", "China", 2));
        Player playerThree = playerRepository.save(new Player("Mima", "Ito", "mima@pong.com", "Japan", 3));

        tournamentMatchRepository.save(new TournamentMatch(
                playerOne,
                playerTwo,
                11,
                7,
                playerOne,
                LocalDateTime.parse("2026-03-19T10:00:00")
        ));

        assertThat(tournamentMatchRepository.existsByPlayerOneIdOrPlayerTwoId(playerOne.getId(), playerOne.getId())).isTrue();
        assertThat(tournamentMatchRepository.existsByPlayerOneIdOrPlayerTwoId(playerTwo.getId(), playerTwo.getId())).isTrue();
        assertThat(tournamentMatchRepository.existsByPlayerOneIdOrPlayerTwoId(playerThree.getId(), playerThree.getId())).isFalse();
    }
}

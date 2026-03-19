package com.dynatrace.pong.repository;

import com.dynatrace.pong.model.TournamentMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentMatchRepository extends JpaRepository<TournamentMatch, Long> {

    boolean existsByPlayerOneIdOrPlayerTwoId(Long playerOneId, Long playerTwoId);
}

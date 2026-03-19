package com.dynatrace.pong.service;

import com.dynatrace.pong.dto.PlayerRequest;
import com.dynatrace.pong.dto.PlayerResponse;
import com.dynatrace.pong.exception.DuplicateEmailException;
import com.dynatrace.pong.exception.PlayerHasMatchesException;
import com.dynatrace.pong.exception.PlayerNotFoundException;
import com.dynatrace.pong.model.Player;
import com.dynatrace.pong.repository.PlayerRepository;
import com.dynatrace.pong.repository.TournamentMatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TournamentMatchRepository tournamentMatchRepository;

    public PlayerService(PlayerRepository playerRepository, TournamentMatchRepository tournamentMatchRepository) {
        this.playerRepository = playerRepository;
        this.tournamentMatchRepository = tournamentMatchRepository;
    }

    public PlayerResponse createPlayer(PlayerRequest request) {
        if (playerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        Player player = new Player(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getCountry(),
                request.getRanking()
        );

        Player saved = playerRepository.save(player);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PlayerResponse getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id));
        return toResponse(player);
    }

    @Transactional(readOnly = true)
    public List<PlayerResponse> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void deletePlayer(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new PlayerNotFoundException(id);
        }

        if (tournamentMatchRepository.existsByPlayerOneIdOrPlayerTwoId(id, id)) {
            throw new PlayerHasMatchesException(id);
        }

        playerRepository.deleteById(id);
    }

    private PlayerResponse toResponse(Player player) {
        return new PlayerResponse(
                player.getId(),
                player.getFirstName(),
                player.getLastName(),
                player.getEmail(),
                player.getCountry(),
                player.getRanking()
        );
    }
}


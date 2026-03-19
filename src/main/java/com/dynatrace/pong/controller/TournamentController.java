package com.dynatrace.pong.controller;

import com.dynatrace.pong.dto.MatchRequest;
import com.dynatrace.pong.dto.MatchResponse;
import com.dynatrace.pong.dto.StandingResponse;
import com.dynatrace.pong.service.TournamentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TournamentController {

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PostMapping("/matches")
    public ResponseEntity<MatchResponse> recordMatch(@Valid @RequestBody MatchRequest request) {
        MatchResponse response = tournamentService.recordMatch(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/standings")
    public ResponseEntity<List<StandingResponse>> getStandings() {
        return ResponseEntity.ok(tournamentService.getStandings());
    }
}

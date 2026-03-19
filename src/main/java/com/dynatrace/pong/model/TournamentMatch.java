package com.dynatrace.pong.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "tournament_matches")
public class TournamentMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_one_id", nullable = false)
    private Player playerOne;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_two_id", nullable = false)
    private Player playerTwo;

    @Column(nullable = false)
    private Integer playerOneScore;

    @Column(nullable = false)
    private Integer playerTwoScore;

    @ManyToOne(optional = false)
    @JoinColumn(name = "winner_id", nullable = false)
    private Player winner;

    @Column(nullable = false)
    private LocalDateTime playedAt;

    public TournamentMatch() {
    }

    public TournamentMatch(
            Player playerOne,
            Player playerTwo,
            Integer playerOneScore,
            Integer playerTwoScore,
            Player winner,
            LocalDateTime playedAt
    ) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.playerOneScore = playerOneScore;
        this.playerTwoScore = playerTwoScore;
        this.winner = winner;
        this.playedAt = playedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(Player playerOne) {
        this.playerOne = playerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(Player playerTwo) {
        this.playerTwo = playerTwo;
    }

    public Integer getPlayerOneScore() {
        return playerOneScore;
    }

    public void setPlayerOneScore(Integer playerOneScore) {
        this.playerOneScore = playerOneScore;
    }

    public Integer getPlayerTwoScore() {
        return playerTwoScore;
    }

    public void setPlayerTwoScore(Integer playerTwoScore) {
        this.playerTwoScore = playerTwoScore;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }
}

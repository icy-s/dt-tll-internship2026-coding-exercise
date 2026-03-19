package com.dynatrace.pong.dto;

import java.time.LocalDateTime;

public class MatchResponse {

    private Long id;
    private Long playerOneId;
    private Long playerTwoId;
    private Integer playerOneScore;
    private Integer playerTwoScore;
    private Long winnerId;
    private LocalDateTime playedAt;

    public MatchResponse() {
    }

    public MatchResponse(
            Long id,
            Long playerOneId,
            Long playerTwoId,
            Integer playerOneScore,
            Integer playerTwoScore,
            Long winnerId,
            LocalDateTime playedAt
    ) {
        this.id = id;
        this.playerOneId = playerOneId;
        this.playerTwoId = playerTwoId;
        this.playerOneScore = playerOneScore;
        this.playerTwoScore = playerTwoScore;
        this.winnerId = winnerId;
        this.playedAt = playedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlayerOneId() {
        return playerOneId;
    }

    public void setPlayerOneId(Long playerOneId) {
        this.playerOneId = playerOneId;
    }

    public Long getPlayerTwoId() {
        return playerTwoId;
    }

    public void setPlayerTwoId(Long playerTwoId) {
        this.playerTwoId = playerTwoId;
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

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }
}

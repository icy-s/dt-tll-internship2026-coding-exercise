package com.dynatrace.pong.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class MatchRequest {

    @NotNull(message = "Player one id is required")
    private Long playerOneId;

    @NotNull(message = "Player two id is required")
    private Long playerTwoId;

    @NotNull(message = "Player one score is required")
    @Min(value = 0, message = "Player one score must be at least 0")
    private Integer playerOneScore;

    @NotNull(message = "Player two score is required")
    @Min(value = 0, message = "Player two score must be at least 0")
    private Integer playerTwoScore;

    public MatchRequest() {
    }

    public MatchRequest(Long playerOneId, Long playerTwoId, Integer playerOneScore, Integer playerTwoScore) {
        this.playerOneId = playerOneId;
        this.playerTwoId = playerTwoId;
        this.playerOneScore = playerOneScore;
        this.playerTwoScore = playerTwoScore;
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
}

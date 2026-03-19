package com.dynatrace.pong.dto;

public class StandingResponse {

    private Long playerId;
    private String firstName;
    private String lastName;
    private Integer matchesPlayed;
    private Integer wins;
    private Integer losses;
    private Integer pointsFor;
    private Integer pointsAgainst;
    private Integer scoreDifference;
    private Integer tournamentPoints;

    public StandingResponse() {
    }

    public StandingResponse(
            Long playerId,
            String firstName,
            String lastName,
            Integer matchesPlayed,
            Integer wins,
            Integer losses,
            Integer pointsFor,
            Integer pointsAgainst,
            Integer scoreDifference,
            Integer tournamentPoints
    ) {
        this.playerId = playerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.matchesPlayed = matchesPlayed;
        this.wins = wins;
        this.losses = losses;
        this.pointsFor = pointsFor;
        this.pointsAgainst = pointsAgainst;
        this.scoreDifference = scoreDifference;
        this.tournamentPoints = tournamentPoints;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(Integer matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getLosses() {
        return losses;
    }

    public void setLosses(Integer losses) {
        this.losses = losses;
    }

    public Integer getPointsFor() {
        return pointsFor;
    }

    public void setPointsFor(Integer pointsFor) {
        this.pointsFor = pointsFor;
    }

    public Integer getPointsAgainst() {
        return pointsAgainst;
    }

    public void setPointsAgainst(Integer pointsAgainst) {
        this.pointsAgainst = pointsAgainst;
    }

    public Integer getScoreDifference() {
        return scoreDifference;
    }

    public void setScoreDifference(Integer scoreDifference) {
        this.scoreDifference = scoreDifference;
    }

    public Integer getTournamentPoints() {
        return tournamentPoints;
    }

    public void setTournamentPoints(Integer tournamentPoints) {
        this.tournamentPoints = tournamentPoints;
    }
}

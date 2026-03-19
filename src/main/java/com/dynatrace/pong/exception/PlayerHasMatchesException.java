package com.dynatrace.pong.exception;

public class PlayerHasMatchesException extends RuntimeException {

    public PlayerHasMatchesException(Long id) {
        super("Player with id " + id + " cannot be deleted because match results already exist");
    }
}

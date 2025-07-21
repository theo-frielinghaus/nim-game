package com.theof.nimgame.domain;

public enum PlayerType {
    HUMAN("Human player"),
    COM("Computer player");


    private final String displayName;

    PlayerType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}

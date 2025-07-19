package com.theof.nimgame.application;

public enum GamelogTemplate {
    GAME_STARTED("New game created and started!"),
    HUMAN_PLAYER_STARTS("Human player starts! Make your move"),
    COM_PLAYER_STARTS("Computer player starts!"),
    COM_PLAYER_TURN("Computer player made their move and took %d sticks!");

    private final String template;

    GamelogTemplate(String template) {
        this.template = template;
    }

    public String format(Object... args) {
        return String.format(template, args);
    }
}

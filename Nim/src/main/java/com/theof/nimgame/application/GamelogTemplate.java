package com.theof.nimgame.application;

public enum GamelogTemplate {
    GAME_STARTED("New game created and started!"),
    HUMAN_PLAYER_STARTS("Human player starts! Make your move"),
    COM_PLAYER_STARTS("Computer player starts!"),
    HUMAN_PLAYER_TURN("Human player made their move and took %d sticks!"),
    COM_PLAYER_TURN("Computer player made their move and took %d sticks! Make your move"),
    HUMAN_PLAYER_WON("Game is over! You won, congratulations!"),
    CON_PLAYER_WON("Game is over! You lost, better luck next time!");

    private final String template;

    GamelogTemplate(String template) {
        this.template = template;
    }

    public String format(Object... args) {
        return String.format(template, args);
    }
}

package com.theof.nimgame.api;

import com.theof.nimgame.application.GameState;

public class GameStateMapper {

    public static GameStateDTO gameStateDTOFrom(GameState gameState) {
        String winner = gameState.winner() != null ? gameState.winner().displayName() : "";
        return new GameStateDTO(gameState.gameId(), gameState.stickCount(), gameState.gamelog(), winner);
    }
}

package com.theof.nimgame.application;

import com.theof.nimgame.domain.Game;
import com.theof.nimgame.infrastructure.GameRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.theof.nimgame.application.GamelogTemplate.COM_PLAYER_STARTS;
import static com.theof.nimgame.application.GamelogTemplate.COM_PLAYER_TURN;
import static com.theof.nimgame.application.GamelogTemplate.CON_PLAYER_WON;
import static com.theof.nimgame.application.GamelogTemplate.GAME_STARTED;
import static com.theof.nimgame.application.GamelogTemplate.HUMAN_PLAYER_STARTS;
import static com.theof.nimgame.application.GamelogTemplate.HUMAN_PLAYER_TURN;
import static com.theof.nimgame.application.GamelogTemplate.HUMAN_PLAYER_WON;

@ApplicationScoped
public class GameService {

    @Inject
    GameRepository gameRepository;

    private static final int NUM_STARTING_STICKS = 13;

    @Transactional
    public Long createGame(String comStrategy) {
        var game = new Game(comStrategy, NUM_STARTING_STICKS);
        gameRepository.persist(game);
        return game.id;
    }

    @Transactional
    public GameState startGame(Long gameId, boolean hasHumanPlayerFirstTurn) {
        var gameLog = new ArrayList<String>();
        var game = gameRepository.findById(gameId);
        var sticksOnPileCount = game.getStickCountOnPile();
        gameLog.add(GAME_STARTED.format());

        if (hasHumanPlayerFirstTurn) {
            gameLog.add(HUMAN_PLAYER_STARTS.format());
            return new GameState(gameId, sticksOnPileCount, List.copyOf(gameLog), null);
        }
        
        gameLog.add(COM_PLAYER_STARTS.format());
        var gameStateAtStart = new GameState(gameId, sticksOnPileCount, List.copyOf(gameLog), null);

        return makeComMove(gameStateAtStart, game);
    }

    @Transactional
    public GameState makeMove(Long gameId, int sticksToTake) {
        var game = gameRepository.findById(gameId);

        GameState gameStateAfterHumanMove = makeHumanMove(sticksToTake, game);

        if (gameStateAfterHumanMove.winner() != null) {
            return gameOver(gameStateAfterHumanMove);
        }

        GameState gameStateAfterComMove = makeComMove(gameStateAfterHumanMove, game);

        if (gameStateAfterComMove.winner() != null) {
            return gameOver(gameStateAfterComMove);
        }

        return gameStateAfterComMove;
    }

    private GameState makeHumanMove(int sticksToTake, Game game) {
        var gamelog = new ArrayList<String>();

        var moveToMake = game.createHumanMove(sticksToTake);
        var stickCountAfterHumanMove = game.makeMove(moveToMake);
        gamelog.add(HUMAN_PLAYER_TURN.format(sticksToTake));

        return new GameState(game.id, stickCountAfterHumanMove, List.copyOf(gamelog), game.getWinner());
    }

    private GameState makeComMove(GameState gameState, Game game) {
        var gamelog = new ArrayList<>(gameState.gamelog());

        var moveToMake = game.createComMove();
        var stickCountAfterComMove = game.makeMove(moveToMake);

        var sticksTaken = gameState.stickCount() - stickCountAfterComMove;

        gamelog.add(COM_PLAYER_TURN.format(sticksTaken));

        return new GameState(gameState.gameId(), stickCountAfterComMove, List.copyOf(gamelog),
            game.getWinner());
    }

    private GameState gameOver(GameState gameState) {
        var gamelog = new ArrayList<>(gameState.gamelog());
        switch (gameState.winner()) {
            case HUMAN -> gamelog.add(HUMAN_PLAYER_WON.format());
            case COM -> gamelog.add(CON_PLAYER_WON.format());
        }
        return new GameState(gameState.gameId(), gameState.stickCount(), List.copyOf(gamelog), gameState.winner());
    }

}

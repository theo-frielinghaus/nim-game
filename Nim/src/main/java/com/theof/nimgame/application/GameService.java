package com.theof.nimgame.application;

import com.theof.nimgame.domain.Game;
import com.theof.nimgame.domain.Move;
import com.theof.nimgame.domain.Pile;
import com.theof.nimgame.domain.PlayerType;
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
        var pile = new Pile(NUM_STARTING_STICKS);
        var game = new Game(comStrategy, pile);
        game.createStrategy();
        gameRepository.persist(game);
        return game.id;
    }

    @Transactional
    public GameState startGame(Long gameId, boolean hasHumanPlayerFirstTurn) {
        var gameLog = new ArrayList<String>();
        var game = gameRepository.findById(gameId);
        var startingPile = game.getPile();

        gameLog.add(GAME_STARTED.format());

        if (hasHumanPlayerFirstTurn) {
            gameLog.add(HUMAN_PLAYER_STARTS.format());
            return new GameState(gameId, startingPile.stickCount(), List.copyOf(gameLog), null);
        }

        gameLog.add(COM_PLAYER_STARTS.format());
        var gameState = new GameState(gameId, startingPile.stickCount(), List.copyOf(gameLog), null );
        return computerMakesMove(gameState,  game);
    }

    @Transactional
    public GameState makeMove(Long gameId, int sticksToTake) {
        var game = gameRepository.findById(gameId);

        GameState gameStateAfterHumanMove = humanMakesMove(sticksToTake, game);

        if (gameStateAfterHumanMove.winner() != null) {
            return getGameOverGameState(gameStateAfterHumanMove);
        }

        GameState gameStateAfterComMove = computerMakesMove(gameStateAfterHumanMove, game);

        if (gameStateAfterComMove.winner() != null) {
            return getGameOverGameState(gameStateAfterComMove);
        }

        return gameStateAfterComMove;
    }

    private GameState humanMakesMove(int sticksToTake, Game game) {
        var gamelog = new ArrayList<String>();
        var move = new Move(PlayerType.HUMAN, sticksToTake);

        Pile pileAfterHumanMove = game.applyMove(move);
        gamelog.add(HUMAN_PLAYER_TURN.format(sticksToTake));

        return new GameState(game.id, pileAfterHumanMove.stickCount(), List.copyOf(gamelog), game.getWinner());
    }

    private GameState computerMakesMove(GameState gameState, Game game) {
        var gamelog = new ArrayList<>(gameState.gamelog());
        var comMove = game.createStrategy().makeMove(game.getPile());

        var pileAfterComMove = game.applyMove(comMove);
        gamelog.add(COM_PLAYER_TURN.format(comMove.sticksToTake()));

        return new GameState(gameState.gameId(), pileAfterComMove.stickCount(), List.copyOf(gamelog),
            game.getWinner());
    }

    private GameState getGameOverGameState(GameState gameState) {
        var gamelog = new ArrayList<>(gameState.gamelog());
        switch (gameState.winner()) {
            case HUMAN -> gamelog.add(HUMAN_PLAYER_WON.format());
            case COM -> gamelog.add(CON_PLAYER_WON.format());
        }
        return new GameState(gameState.gameId(), gameState.stickCount(), List.copyOf(gamelog), gameState.winner());
    }

}

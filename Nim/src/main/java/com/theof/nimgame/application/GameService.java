package com.theof.nimgame.application;

import com.theof.nimgame.api.GameStateDTO;
import com.theof.nimgame.api.MoveDTO;
import com.theof.nimgame.api.SettingsDTO;
import com.theof.nimgame.domain.Game;
import com.theof.nimgame.domain.Move;
import com.theof.nimgame.domain.Pile;
import com.theof.nimgame.infrastructure.GameRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.theof.nimgame.application.GamelogTemplate.COM_PLAYER_STARTS;
import static com.theof.nimgame.application.GamelogTemplate.COM_PLAYER_TURN;
import static com.theof.nimgame.application.GamelogTemplate.GAME_STARTED;
import static com.theof.nimgame.application.GamelogTemplate.HUMAN_PLAYER_STARTS;
import static com.theof.nimgame.application.GamelogTemplate.HUMAN_PLAYER_TURN;

@ApplicationScoped
public class GameService {

    @Inject
    GameRepository gameRepository;

    private static final int NUM_STARTING_STICKS = 13;

    @Transactional
    public Long createGame(SettingsDTO settings) {
        var pile = new Pile(NUM_STARTING_STICKS);
        var game = new Game(settings.comStrategy(), pile);
        game.createStrategy();
        gameRepository.persist(game);
        return game.id;
    }

    @Transactional
    public GameStateDTO startGame(Long gameId, boolean hasHumanPlayerFirstTurn) {
        var gameLog = new ArrayList<String>();
        var game = gameRepository.findById(gameId);
        var startingPile = game.getPile();

        gameLog.add(GAME_STARTED.format());

        if (hasHumanPlayerFirstTurn) {
            gameLog.add(HUMAN_PLAYER_STARTS.format());
            return new GameStateDTO(gameId, startingPile.stickCount(), gameLog);
        }
        gameLog.add(COM_PLAYER_STARTS.format());
        Pile pileAfterMove = computerMakesMove(game, gameLog);

        return new GameStateDTO(gameId, pileAfterMove.stickCount(), gameLog);
    }

    }

    private Pile computerMakesMove(Game game, List<String> gameLog) {
        var comMove = game.createStrategy().makeMove(game.getPile());

        gameLog.add(COM_PLAYER_TURN.format(comMove.sticksToTake()));

        return game.applyMove(comMove);
    }


}

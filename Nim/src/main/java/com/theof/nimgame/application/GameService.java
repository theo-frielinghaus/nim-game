package com.theof.nimgame.application;

import com.theof.nimgame.api.GameState;
import com.theof.nimgame.api.NewGameSettings;
import com.theof.nimgame.domain.Game;
import com.theof.nimgame.domain.Pile;
import com.theof.nimgame.infrastructure.GameRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.theof.nimgame.application.GamelogTemplate.COM_PLAYER_STARTS;
import static com.theof.nimgame.application.GamelogTemplate.COM_PLAYER_TURN;
import static com.theof.nimgame.application.GamelogTemplate.GAME_STARTED;
import static com.theof.nimgame.application.GamelogTemplate.HUMAN_PLAYER_STARTS;

@ApplicationScoped
public class GameService {

    @Inject
    GameRepository gameRepository;

    private static final Random random = new Random();

    private static final int NUM_STARTING_STICKS = 13;


    @Transactional
    public long createGame(NewGameSettings settings) {
        var pile = new Pile(NUM_STARTING_STICKS);
        var game = new Game(settings.comStrategy(), pile, decideStartingPlayer());
        gameRepository.persist(game);
        return game.id;
    }

    @Transactional
    public GameState startGame(long gameId) {
        var gameLog = new ArrayList<String>();
        var game = gameRepository.findById(gameId);

        Pile pile = game.start();
        gameLog.add(GAME_STARTED.format());

        if (!game.isComPlayerFirst()) {
            gameLog.add(HUMAN_PLAYER_STARTS.format());
            return new GameState(gameId, pile.stickCount(), gameLog);
        }
        gameLog.add(COM_PLAYER_STARTS.format());
        var stickDifference = NUM_STARTING_STICKS - pile.stickCount();
        gameLog.add(COM_PLAYER_TURN.format(stickDifference));
        return new GameState(gameId, pile.stickCount(), gameLog);
    }

    private boolean decideStartingPlayer() {
        // Currently just a coin-flip
        return random.nextBoolean();
    }
}

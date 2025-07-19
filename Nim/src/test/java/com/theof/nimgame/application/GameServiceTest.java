package com.theof.nimgame.application;

import com.theof.nimgame.api.NewGameSettings;
import com.theof.nimgame.domain.Game;
import com.theof.nimgame.domain.RandomMoveStrategy;
import com.theof.nimgame.infrastructure.GameRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.theof.nimgame.application.GamelogTemplate.COM_PLAYER_STARTS;
import static com.theof.nimgame.application.GamelogTemplate.COM_PLAYER_TURN;
import static com.theof.nimgame.application.GamelogTemplate.GAME_STARTED;
import static com.theof.nimgame.application.GamelogTemplate.HUMAN_PLAYER_STARTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@QuarkusTest
@Transactional
class GameServiceTest {

    @Inject
    GameService testee;

    @Inject
    GameRepository gameRepository;

    final int NUM_STARTING_STICKS = 13;

    @BeforeAll
    static void setUp() {
        Assertions.setAllowExtractingPrivateFields(true);
    }

    @Nested
    class GameCreation {

        @Test
        void game_is_created_and_persisted() {
            var validSettings = new NewGameSettings("random");
            List<Game> oldGames = gameRepository.listAll();

            long id = testee.createGame(validSettings);

            Game persistedGame = gameRepository.findById(id);

            assertThat(oldGames).doesNotContain(persistedGame);

            assertThat(persistedGame)
                    .isNotNull()
                .extracting("strategy")
                    .isInstanceOf(RandomMoveStrategy.class);

            assertThat(persistedGame)
                .extracting("pile")
                .extracting("stickCount")
                    .isEqualTo(13);

        }

        @Test
        void game_with_invalid_settings_is_not_persisted() {
            var validSettings = new NewGameSettings("llm");
            List<Game> oldGames = gameRepository.listAll();

            assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
                testee.createGame(validSettings);
            }).withMessage("Unknown strategy: llm");
            List<Game> updatedGames = gameRepository.listAll();
            assertThat(oldGames).containsAll(updatedGames);
        }



    }

    @Nested
    class GameStart{
        @Test
        void human_player_first() {
            var gameState = testee.startGame(-1);

            assertThat(gameState).isNotNull();
            assertThat(gameState.gameId()).isEqualTo(-1);
            assertThat(gameState.stickCount()).isEqualTo(NUM_STARTING_STICKS);
            assertThat(gameState.gamelog()).contains(GAME_STARTED.format(), HUMAN_PLAYER_STARTS.format());
            assertThat(gameState.gamelog()).doesNotContain(COM_PLAYER_STARTS.format());

        }

        @Test
        void computer_player_first() {
            var gameState = testee.startGame(-2);
            var stickDifference = NUM_STARTING_STICKS - gameState.stickCount();

            assertThat(gameState).isNotNull();
            assertThat(gameState.gameId()).isEqualTo(-2);
            assertThat(gameState.stickCount()).isNotEqualTo(NUM_STARTING_STICKS);
            assertThat(stickDifference).isBetween(1,3);
            assertThat(gameState.gamelog()).contains(GAME_STARTED.format(), COM_PLAYER_STARTS.format(),
                COM_PLAYER_TURN.format(stickDifference));
            assertThat(gameState.gamelog()).doesNotContain(HUMAN_PLAYER_STARTS.format());

        }

    }

}
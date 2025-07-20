package com.theof.nimgame.application;

import com.theof.nimgame.api.GameStateDTO;
import com.theof.nimgame.api.MoveDTO;
import com.theof.nimgame.api.SettingsDTO;
import com.theof.nimgame.domain.Game;
import com.theof.nimgame.domain.RandomMoveStrategy;
import com.theof.nimgame.infrastructure.GameRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Random;

import static com.theof.nimgame.application.GamelogTemplate.COM_PLAYER_STARTS;
import static com.theof.nimgame.application.GamelogTemplate.COM_PLAYER_TURN;
import static com.theof.nimgame.application.GamelogTemplate.GAME_STARTED;
import static com.theof.nimgame.application.GamelogTemplate.HUMAN_PLAYER_STARTS;
import static com.theof.nimgame.application.GamelogTemplate.HUMAN_PLAYER_TURN;
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
        //TODO: check if necessary
        Assertions.setAllowExtractingPrivateFields(true);
    }

    @Nested
    class GameCreation {

        @Test
        void game_is_created_and_persisted() {
            var validSettings = new SettingsDTO("random");
            List<Game> oldGames = gameRepository.listAll();

            Long id = testee.createGame(validSettings);

            Game persistedGame = gameRepository.findById(id);

            assertThat(oldGames).doesNotContain(persistedGame);

            assertThat(persistedGame)
                .isNotNull()
                .extracting("comPlayerStrategyName")
                .isEqualTo("random");

            assertThat(persistedGame)
                .extracting("pile")
                .extracting("stickCount")
                .isEqualTo(13);

        }

        @Test
        void game_with_invalid_settings_is_not_persisted() {
            var validSettings = new SettingsDTO("llm");
            List<Game> oldGames = gameRepository.listAll();

            assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
                testee.createGame(validSettings);
            }).withMessage("Unknown strategy: llm");
            List<Game> updatedGames = gameRepository.listAll();
            assertThat(oldGames).containsAll(updatedGames);
        }

    }

    @Nested
    class GameStart {
        @Test
        void human_player_first() {

            final Long gameId = -3L;
            var gameState = testee.startGame(gameId, true);
            Game gameAfterStart = gameRepository.findById(gameId);

            assertThat(gameState).isNotNull();
            assertThat(gameState.gameId()).isEqualTo(gameId);
            assertThat(gameState.stickCount()).isEqualTo(NUM_STARTING_STICKS);
            assertThat(gameState.gamelog()).contains(GAME_STARTED.format(), HUMAN_PLAYER_STARTS.format());
            assertThat(gameState.gamelog()).doesNotContain(COM_PLAYER_STARTS.format());

            assertThat(gameAfterStart)
                .extracting("pile")
                .extracting("stickCount")
                .isEqualTo(NUM_STARTING_STICKS);

        }

        @Test
        void computer_player_first() {
            final Long gameId = -4L;

            var gameState = testee.startGame(gameId, false);

            var stickDifference = NUM_STARTING_STICKS - gameState.stickCount();
            Game gameAfterStart = gameRepository.findById(gameId);

            assertThat(gameState).isNotNull();
            assertThat(gameState.gameId()).isEqualTo(gameId);
            assertThat(gameState.stickCount()).isNotEqualTo(NUM_STARTING_STICKS);
            assertThat(stickDifference).isBetween(1, 3);
            assertThat(gameState.gamelog()).contains(GAME_STARTED.format(), COM_PLAYER_STARTS.format(),
                COM_PLAYER_TURN.format(stickDifference));
            assertThat(gameState.gamelog()).doesNotContain(HUMAN_PLAYER_STARTS.format());

            assertThat(gameAfterStart)
                .extracting("pile")
                .extracting("stickCount")
                .isIn(12, 11, 10);

        }

    }

    @Nested
    class GameMoves {

        @Test
        void human_player_move_is_valid() {
            final Long gameId = -5L;
            var moveToMake = new MoveDTO(3);
            GameStateDTO gameState = testee.makeMove(gameId, moveToMake);
            Game gameAfterMove = gameRepository.findById(gameId);
            var numSticksComputerMove = NUM_STARTING_STICKS - moveToMake.sticksToTake() - gameState.stickCount();

            assertThat(gameState).isNotNull();
            assertThat(gameState.gameId()).isEqualTo(gameId);
            assertThat(gameState.stickCount()).isBetween(7, 9);
            assertThat(gameState.gamelog()).contains(HUMAN_PLAYER_TURN.format(moveToMake.sticksToTake()),
                COM_PLAYER_TURN.format(numSticksComputerMove));

            assertThat(gameAfterMove)
                .extracting("pile")
                .extracting("stickCount")
                .isIn(7, 8, 9);
        }

        @Test
        void human_player_move_is_invalid() {
            final Long gameId = -6L;
            var moveToMake = new MoveDTO(10);

            assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
                GameStateDTO gameState = testee.makeMove(gameId, moveToMake);

                assertThat(gameState).isNull();
                Game gameAfterMove = gameRepository.findById(gameId);

                assertThat(gameAfterMove)
                    .extracting("pile")
                    .extracting("stickCount")
                    .isEqualTo(NUM_STARTING_STICKS);
            });

        }
    }

/*        @Test
        void human_player_takes_negative_sticks() {
        }

        @Test
        void human_player_takes_zero_sticks() {
        }

        @Test
        void human_player_takes_more_than_three_sticks() {
        }*/


}
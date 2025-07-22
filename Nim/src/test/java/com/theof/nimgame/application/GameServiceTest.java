package com.theof.nimgame.application;

import com.theof.nimgame.domain.Game;
import com.theof.nimgame.domain.PlayerType;
import com.theof.nimgame.infrastructure.GameRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.theof.nimgame.application.GamelogTemplate.COM_PLAYER_STARTS;
import static com.theof.nimgame.application.GamelogTemplate.COM_PLAYER_TURN;
import static com.theof.nimgame.application.GamelogTemplate.CON_PLAYER_WON;
import static com.theof.nimgame.application.GamelogTemplate.GAME_STARTED;
import static com.theof.nimgame.application.GamelogTemplate.HUMAN_PLAYER_STARTS;
import static com.theof.nimgame.application.GamelogTemplate.HUMAN_PLAYER_TURN;
import static com.theof.nimgame.application.GamelogTemplate.HUMAN_PLAYER_WON;
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
    @Inject
    GameService gameService;

    @BeforeAll
    static void setUp() {
        //TODO: check if necessary
        Assertions.setAllowExtractingPrivateFields(true);
    }

    @Nested
    class GameRetrieval {

        @Test
        void current_game_state_gets_retrieved() {
            // given
            final var GAME_ID = -6L;

            // when
            var gameState = testee.getGameState(GAME_ID);

            // then
            assertThat(gameState).isNotNull();
            assertThat(gameState.gameId()).isEqualTo(GAME_ID);
            assertThat(gameState.gamelog()).isEmpty();
            assertThat(gameState.winner()).isNull();
            assertThat(gameState.stickCount()).isEqualTo(13);
        }

        @Test
        void game_cannot_be_found() {
            // given
            final var GAME_ID = -999L;

            //when
            assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> {
                var gameState = testee.getGameState(GAME_ID);

                // then
                assertThat(gameState).isNull();
            }).withMessage(String.format("Game with id %d doesn't exist.", GAME_ID));
        }
    }

    @Nested
    class GameCreation {

        @Test
        void game_is_created_and_persisted() {
            // given
            final var VALID_STRATEGY_NAME = "random";
            List<Game> oldGames = gameRepository.listAll();

            // when
            Long id = testee.createGame(VALID_STRATEGY_NAME);

            // then
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
            // given
            final var INVALID_STRATEGY_NAME = "llm";
            List<Game> oldGames = gameRepository.listAll();

            // when
            assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
                testee.createGame(INVALID_STRATEGY_NAME);

                // then
            }).withMessage("Unknown strategy: llm");
            List<Game> updatedGames = gameRepository.listAll();
            assertThat(oldGames).containsAll(updatedGames);
        }

    }

    @Nested
    class GameStart {
        @Test
        void human_player_first() {
            // given
            final var GAME_ID = -3L;

            // when
            var gameState = testee.startGame(GAME_ID, true);
            Game gameAfterStart = gameRepository.findById(GAME_ID);

            // then
            assertThat(gameState).isNotNull();
            assertThat(gameState.gameId()).isEqualTo(GAME_ID);
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
            // given
            final var GAME_ID = -4L;

            // when
            var gameState = testee.startGame(GAME_ID, false);

            // then
            var stickDifference = NUM_STARTING_STICKS - gameState.stickCount();
            Game gameAfterStart = gameRepository.findById(GAME_ID);

            assertThat(gameState).isNotNull();
            assertThat(gameState.gameId()).isEqualTo(GAME_ID);
            assertThat(gameState.stickCount()).isNotEqualTo(NUM_STARTING_STICKS);
            assertThat(gameState.gamelog()).contains(GAME_STARTED.format(), COM_PLAYER_STARTS.format(),
                COM_PLAYER_TURN.format(stickDifference));
            assertThat(gameState.gamelog()).doesNotContain(HUMAN_PLAYER_STARTS.format());

            assertThat(stickDifference).isBetween(1, 3);

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
            // given
            final var GAME_ID = -5L;
            final var STICKS_TO_TAKE = 3;

            // when
            GameState gameState = testee.makeMove(GAME_ID, STICKS_TO_TAKE);

            // then
            Game gameAfterMove = gameRepository.findById(GAME_ID);
            var numSticksComputerMove = NUM_STARTING_STICKS - STICKS_TO_TAKE - gameState.stickCount();

            assertThat(gameState).isNotNull();
            assertThat(gameState.gameId()).isEqualTo(GAME_ID);
            assertThat(gameState.stickCount()).isBetween(7, 9);
            assertThat(gameState.gamelog()).contains(HUMAN_PLAYER_TURN.format(STICKS_TO_TAKE),
                COM_PLAYER_TURN.format(numSticksComputerMove));

            assertThat(gameAfterMove)
                .extracting("pile")
                .extracting("stickCount")
                .isIn(7, 8, 9);
        }

        @Test
        void human_player_move_is_invalid() {
            // given
            final var GAME_ID = -6L;
            final var STICKS_TO_TAKE = 10;

            // when
            assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
                GameState gameState = testee.makeMove(GAME_ID, STICKS_TO_TAKE);

                // then
                assertThat(gameState).isNull();
                Game gameAfterMove = gameRepository.findById(GAME_ID);

                assertThat(gameAfterMove)
                    .extracting("pile")
                    .extracting("stickCount")
                    .isEqualTo(NUM_STARTING_STICKS);
            });
        }
    }

    @Nested
    class GameEndingMoves {

        @Test
        void human_player_loses() {
            // given
            final var GAME_ID = -9L;
            final var STICKS_TO_TAKE = 1;

            // when
            GameState gameState = testee.makeMove(GAME_ID, STICKS_TO_TAKE);

            // then
            Game gameAfterMove = gameRepository.findById(GAME_ID);

            assertThat(gameState).isNotNull();
            assertThat(gameState.gameId()).isEqualTo(GAME_ID);
            assertThat(gameState.stickCount()).isEqualTo(0);
            assertThat(gameState.gamelog()).contains(HUMAN_PLAYER_TURN.format(STICKS_TO_TAKE),
                CON_PLAYER_WON.format());

            assertThat(gameAfterMove)
                .extracting("pile")
                .extracting("stickCount")
                .isEqualTo(0);
            assertThat(gameAfterMove.getWinner()).isEqualTo(PlayerType.COM);
        }

        @Test
        void human_player_wins() {
            // given
            final var GAME_ID = -10L;
            final var STICKS_TO_TAKE = 1;

            // when
            GameState gameState = testee.makeMove(GAME_ID, STICKS_TO_TAKE);

            // then
            Game gameAfterMove = gameRepository.findById(GAME_ID);

            assertThat(gameState).isNotNull();
            assertThat(gameState.gameId()).isEqualTo(GAME_ID);
            assertThat(gameState.stickCount()).isEqualTo(0);
            assertThat(gameState.gamelog()).contains(HUMAN_PLAYER_TURN.format(STICKS_TO_TAKE),
                HUMAN_PLAYER_WON.format());

            assertThat(gameAfterMove)
                .extracting("pile")
                .extracting("stickCount")
                .isEqualTo(0);
            assertThat(gameAfterMove.getWinner()).isEqualTo(PlayerType.HUMAN);
        }

        @Test
        void no_moves_after_game_ended() {
            // given
            final var GAME_ID = -11L;
            final var STICKS_TO_TAKE = 2;

            // when
            assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
                GameState gameState = testee.makeMove(GAME_ID, STICKS_TO_TAKE);

                // then
                assertThat(gameState).isNull();

                Game gameAfterMove = gameRepository.findById(GAME_ID);

                assertThat(gameAfterMove)
                    .extracting("pile")
                    .extracting("stickCount")
                    .isEqualTo(0);
                assertThat(gameAfterMove.getWinner()).isEqualTo("human");
            });

        }
    }
}


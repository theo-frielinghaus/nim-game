package com.theof.nimgame.domain;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@QuarkusTest
class GameTest {
    private static final int NUM_STARTING_STICKS = 13;

    @Nested
    class GameMoves {
        private final PlayerType player = PlayerType.HUMAN;

        @Test
        void remove_some_sticks_from_pile() {
            var game = new Game("random", NUM_STARTING_STICKS);
            var move = new MoveImpl(player,3);

            int stickCountAfterMove = game.makeMove(move);

            assertThat(stickCountAfterMove).isNotEqualTo(NUM_STARTING_STICKS);
            assertThat(stickCountAfterMove).isEqualTo(NUM_STARTING_STICKS - move.sticksToTake());
        }

        @Test
        void no_move_when_game_has_ended() {
            var game = new Game("random", 1);
            var losingMove = new MoveImpl(player, 1);
            var additionalMove = new MoveImpl(PlayerType.COM, 1);
            game.makeMove(losingMove);

            assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {

                int stickCountAfterMove = game.makeMove(additionalMove);

            }).withMessage("Game is already over! No further moves possible.");
        }

        @Test
        void last_possible_move_by_human() {
            var game = new Game("random", 1);
            var move = new MoveImpl(player, 1);

            int stickCountAfterMove = game.makeMove(move);

            assertThat(stickCountAfterMove).isEqualTo(0);
            assertThat(game.getWinner()).isEqualTo(PlayerType.COM);
        }

        @Test
        void last_possible_move_by_computer() {
            var game = new Game("random", 1);
            var move = new MoveImpl(PlayerType.COM, 1);

            int stickCountAfterMove = game.makeMove(move);

            assertThat(stickCountAfterMove).isEqualTo(0);
            assertThat(game.getWinner()).isEqualTo(PlayerType.HUMAN);
        }

        @Test
        void impossible_to_remove_more_sticks_than_pile_has() {
            var game = new Game("random", 2);
            var move = new MoveImpl(player,3);

            assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
                int stickCountAfterMove = game.makeMove(move);
            }).withMessage("A pile cannot have a negative amount of sticks!");

        }

    }

}
package com.theof.nimgame.domain;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@QuarkusTest
class GameTest {
    Pile startingPile = new Pile(13);

    @Nested
    class GameMoves {
        private final PlayerType player = PlayerType.HUMAN;

        @Test
        void remove_some_sticks_from_pile() {
            var game = new Game("random", startingPile);
            var move = new Move(player,3);

            Pile pileAfterMove = game.applyMove(move);

            assertThat(pileAfterMove).isNotNull();
            assertThat(pileAfterMove).isNotEqualTo(startingPile);
            assertThat(pileAfterMove.stickCount()).isEqualTo(startingPile.stickCount() - move.sticksToTake());
        }

        @Test
        void no_move_when_game_has_ended() {
            var game = new Game("random", new Pile(1));
            var losingMove = new Move(player, 1);
            var additionalMove = new Move(PlayerType.COM, 1);
            game.applyMove(losingMove);


            assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {

                Pile pileAfterMove = game.applyMove(additionalMove);

                assertThat(pileAfterMove).isNull();
            }).withMessage("Game is already over! No further moves possible.");
        }

        @Test
        void last_possible_move_by_human() {
            var game = new Game("random", new Pile(1));
            var move = new Move(player, 1);

            Pile pileAfterMove = game.applyMove(move);

            assertThat(pileAfterMove).isNotNull();
            assertThat(pileAfterMove.stickCount()).isEqualTo(0);
            assertThat(game.getWinner()).isEqualTo(PlayerType.COM);
        }

        @Test
        void last_possible_move_by_computer() {
            var game = new Game("random", new Pile(1));
            var move = new Move(PlayerType.COM, 1);

            Pile pileAfterMove = game.applyMove(move);

            assertThat(pileAfterMove).isNotNull();
            assertThat(pileAfterMove.stickCount()).isEqualTo(0);
            assertThat(game.getWinner()).isEqualTo(PlayerType.HUMAN);
        }

        @Test
        void impossible_to_remove_more_sticks_than_pile_has() {
            var game = new Game("random", new Pile(2));
            var move = new Move(player,3);

            assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {

                Pile pileAfterMove = game.applyMove(move);

                assertThat(pileAfterMove).isNull();
            }).withMessage("A pile cannot have a negative amount of sticks!");

        }

    }

}
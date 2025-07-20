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

        @Test
        void removing_some_sticks_from_pile() {
            var game = new Game("random", startingPile);
            var move = new Move(3);

            Pile pileAfterMove = game.applyMove(move);

            assertThat(pileAfterMove).isNotNull();
            assertThat(pileAfterMove).isNotEqualTo(startingPile);
            assertThat(pileAfterMove.stickCount()).isEqualTo(startingPile.stickCount() - move.sticksToTake());
        }

        @Test
        void removing_the_last_stick_from_pile() {
        }

        @Test
        void impossible_to_remove_more_sticks_than_pile_has() {
            var game = new Game("random", new Pile(2));
            var move = new Move(3);

            assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
                Pile pileAfterMove = game.applyMove(move);
                assertThat(pileAfterMove).isNull();
            }).withMessage("A pile cannot have a negative amount of sticks!");

        }

    }

}
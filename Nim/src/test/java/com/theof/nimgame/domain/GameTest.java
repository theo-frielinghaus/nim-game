package com.theof.nimgame.domain;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


@QuarkusTest
class GameTest {

    @Nested
    class GameStart {
        Pile startingPile = new Pile(13);
        @Test
        void human_player_has_first_turn() {
            var game = new Game("random", startingPile, false);
            Pile updatedPile = game.start();

            assertThat(updatedPile).isEqualTo(startingPile);
        }

        @Test
        void computer_player_has_first_turn() {
            var game = new Game("random", startingPile, true);
            Pile updatedPile = game.start();

            int stickDifference = startingPile.stickCount() - updatedPile.stickCount();

            assertThat(updatedPile).isNotEqualTo(startingPile);
            assertThat(stickDifference).isBetween(1,3);

        }
    }


}
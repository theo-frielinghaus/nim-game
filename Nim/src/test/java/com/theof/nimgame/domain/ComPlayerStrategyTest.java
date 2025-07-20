package com.theof.nimgame.domain;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@QuarkusTest
class ComPlayerStrategyTest {

    @Test
    void no_strategy_with_invalid_name(){
        final var STRATEGY_NAME = "llm";

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            ComPlayerStrategy strategy = ComPlayerStrategy.fromType(STRATEGY_NAME);
        }).withMessage("Unknown strategy: llm");
    }

    @Nested
    class RandomMoveStrategyTest {

        @Test
        void random_move_strategy_creation() {
            final var STRATEGY_NAME = "Random";

            ComPlayerStrategy testee = ComPlayerStrategy.fromType(STRATEGY_NAME);
            assertThat(testee).isExactlyInstanceOf(RandomMoveStrategy.class);
        }

        @RepeatedTest(10)
        void valid_random_move() {
            var pileBeforeMove = new Pile(10);
            final var STRATEGY_NAME = "Random";
            ComPlayerStrategy testee = ComPlayerStrategy.fromType(STRATEGY_NAME);

            var move = testee.makeMove(pileBeforeMove);

            assertThat(move).isNotNull();
            assertThat(move.sticksToTake()).isBetween(1,3);
        }

        @Test
        void valid_random_move_with_3_sticks_on_pile() {};

        @Test
        void valid_random_move_with_2_sticks_on_pile() {};

        @Test
        void last_possible_move_with_1_stick_on_pile() {};
    }

    @Nested
    class OptimalMoveStrategyTest {

        @Test
        void optimal_move_strategy_creation() {
            final var STRATEGY_NAME = "OPTIMAL";

            ComPlayerStrategy testee = ComPlayerStrategy.fromType(STRATEGY_NAME);
            assertThat(testee).isExactlyInstanceOf(OptimalMoveStrategy.class);
        }
    }
}
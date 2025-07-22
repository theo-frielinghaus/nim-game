package com.theof.nimgame.domain;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
        final String STRATEGY_NAME = "Random";

        @Test
        void random_move_strategy_creation() {
            ComPlayerStrategy testee = ComPlayerStrategy.fromType(STRATEGY_NAME);
            assertThat(testee).isExactlyInstanceOf(RandomMoveStrategy.class);
        }

        @RepeatedTest(10)
        void valid_random_move() {
            var pileBeforeMove = new Pile(10);
            ComPlayerStrategy testee = ComPlayerStrategy.fromType(STRATEGY_NAME);

            var move = testee.computeMove(pileBeforeMove);

            assertThat(move).isNotNull();
            assertThat(move.sticksToTake()).isBetween(1,3);
        }

        @Test
        void valid_random_move_with_2_sticks_on_pile() {
            var pileBeforeMove = new Pile(2);
            ComPlayerStrategy testee = ComPlayerStrategy.fromType(STRATEGY_NAME);

            var move = testee.computeMove(pileBeforeMove);

            assertThat(move).isNotNull();
            assertThat(move.sticksToTake()).isBetween(1,2);
        };

        @Test
        void last_possible_move_with_1_stick_on_pile() {
            var pileBeforeMove = new Pile(1);
            ComPlayerStrategy testee = ComPlayerStrategy.fromType(STRATEGY_NAME);

            var move = testee.computeMove(pileBeforeMove);

            assertThat(move).isNotNull();
            assertThat(move.sticksToTake()).isEqualTo(1);
        };
    }

    @Nested
    class OptimalMoveStrategyTest {
        final String STRATEGY_NAME = "optimal";

        @Test
        void optimal_move_strategy_creation() {

            ComPlayerStrategy testee = ComPlayerStrategy.fromType(STRATEGY_NAME);
            assertThat(testee).isExactlyInstanceOf(OptimalMoveStrategy.class);
        }


        @ParameterizedTest
        @ValueSource(ints = {10,11,12})
        void optimal_early_game_move_is_leaving_9_on_pile(int stickCount) {
            var pileBeforeMove = new Pile(stickCount);
            ComPlayerStrategy testee = ComPlayerStrategy.fromType(STRATEGY_NAME);

            var move = testee.computeMove(pileBeforeMove);

            assertThat(move).isNotNull();
            assertThat(move.sticksToTake() + 9).isEqualTo(stickCount);
        }

        @ParameterizedTest
        @ValueSource(ints = {6,7,8})
        void optimal_mid_game_move_is_leaving_5_on_pile(int stickCount) {
            var pileBeforeMove = new Pile(stickCount);
            ComPlayerStrategy testee = ComPlayerStrategy.fromType(STRATEGY_NAME);

            var move = testee.computeMove(pileBeforeMove);

            assertThat(move).isNotNull();
            assertThat(move.sticksToTake() + 5).isEqualTo(stickCount);
        }

        @ParameterizedTest
        @ValueSource(ints = {2,3,4})
        void optimal_mid_game_move_is_leaving_1_on_pile(int stickCount) {
            var pileBeforeMove = new Pile(stickCount);
            ComPlayerStrategy testee = ComPlayerStrategy.fromType(STRATEGY_NAME);

            var move = testee.computeMove(pileBeforeMove);

            assertThat(move).isNotNull();
            assertThat(move.sticksToTake() + 1).isEqualTo(stickCount);
        }

        @ParameterizedTest
        @ValueSource(ints = {5,9,13})
        void optimal_move_from_losing_position_is_take_1_from_pile(int stickCount){
            var pileBeforeMove = new Pile(stickCount);
            ComPlayerStrategy testee = ComPlayerStrategy.fromType(STRATEGY_NAME);

            var move = testee.computeMove(pileBeforeMove);

            assertThat(move).isNotNull();
            assertThat(move.sticksToTake()).isEqualTo(1);
        }
    }
}
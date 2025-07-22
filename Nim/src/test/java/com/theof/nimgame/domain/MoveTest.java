package com.theof.nimgame.domain;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@QuarkusTest
class MoveTest {
    private final PlayerType player = PlayerType.HUMAN;

    @Test
    void move_with_one_stick_to_take(){
        var move = new MoveImpl(player,1);
        assertThat(move).isNotNull().extracting("sticksToTake").isEqualTo(1);
    }
    @Test
    void move_with_two_sticks_to_take(){
        var move = new MoveImpl(player,2);
        assertThat(move).isNotNull().extracting("sticksToTake").isEqualTo(2);
    }
    @Test
    void move_with_three_sticks_to_take(){
        var move = new MoveImpl(player,3);
        assertThat(move).isNotNull().extracting("sticksToTake").isEqualTo(3);
    }
    @Test
    void move_with_negative_sticks_to_take_is_impossible(){
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            var move = new MoveImpl(player,-1);
        }).withMessage("A move must take 1, 2 or 3 sticks from the pile!");
    }
    @Test
    void move_with_zero_sticks_to_take_is_impossible(){
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            var move = new MoveImpl(player,0);
        }).withMessage("A move must take 1, 2 or 3 sticks from the pile!");
    }
    @Test
    void move_with_four_sticks_to_take_is_impossible(){
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            var move = new MoveImpl(player,4);
        }).withMessage("A move must take 1, 2 or 3 sticks from the pile!");
    }
    @Test
    void move_without_player_is_impossible(){
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            var move = new MoveImpl(null,2);
        }).withMessage("A move must have a player who makes the move!");
    }
}
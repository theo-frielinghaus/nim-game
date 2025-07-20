package com.theof.nimgame.domain;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@QuarkusTest
class PileTest {

    @Test
    void no_pile_with_negative_amount_of_sticks() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            var pile = new Pile(-1);
        }).withMessage("A stickCount cannot have a negative amount of sticks!");
    }

    @Test
    void pile_with_no_sticks() {
        //An empty stickCount should be possible
        var pile = new Pile(0);
        assertThat(pile).isNotNull().extracting("stickCount").isEqualTo(0);
    }

    @Test
    void pile_with_reasonable_amount_of_sticks() {
        var pile = new Pile(10);
        assertThat(pile).isNotNull().extracting("stickCount").isEqualTo(10);
    }

    @Test
    void pile_with_unreasonable_amount_of_sticks() {
        // The stickCount doesn't know about the rules of the game
        var pile = new Pile(9999999);
        assertThat(pile).isNotNull().extracting("stickCount").isEqualTo(9999999);


    }

}
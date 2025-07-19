package com.theof.nimgame.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record Pile(int stickCount) {
    public Pile {
        if (stickCount < 0) throw new IllegalArgumentException("A stickCount cannot have a negative amount of sticks!");
    }
}

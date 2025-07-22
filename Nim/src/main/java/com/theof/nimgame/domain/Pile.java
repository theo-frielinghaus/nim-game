package com.theof.nimgame.domain;

import jakarta.persistence.Embeddable;

@Embeddable
record Pile(int stickCount) {
    public Pile {
        if (stickCount < 0) throw new IllegalArgumentException("A pile cannot have a negative amount of sticks!");
    }

    public Pile takeSticks(int sticksToTake) {
        return new Pile(stickCount - sticksToTake);
    }
}

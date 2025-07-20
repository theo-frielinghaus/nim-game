package com.theof.nimgame.domain;

public record Move(int sticksToTake) {
    public Move {
        if (sticksToTake < 1 || sticksToTake > 3) throw new IllegalArgumentException("A move must take 1, 2 or 3 sticks from the pile!");
    }
}

package com.theof.nimgame.domain;

public record Move(PlayerType player, int sticksToTake ) {
    public Move {
        if (sticksToTake < 1 || sticksToTake > 3) throw new IllegalArgumentException("A move must take 1, 2 or 3 sticks from the pile!");
        if (player == null) throw new IllegalArgumentException("A move must have a player who makes the move!");
    }
}

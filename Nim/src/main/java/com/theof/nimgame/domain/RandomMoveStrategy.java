package com.theof.nimgame.domain;

import java.util.Random;

public class RandomMoveStrategy implements ComPlayerStrategy {
    private final Random random;

    public RandomMoveStrategy() {
        random = new Random();
    }

    @Override
    public Move makeMove(Pile pile) {
       var maxSticksToTake = Math.min(pile.stickCount(), 3);
        int numSticksToTake = random.nextInt(1, maxSticksToTake + 1);
        return new Move(PlayerType.COM, numSticksToTake);
    }
}

package com.theof.nimgame.domain;

import java.util.Random;

public class RandomMoveStrategy implements ComPlayerStrategy {
    private final Random random;

    public RandomMoveStrategy() {
        random = new Random();
    }

    @Override
    public Move makeMove(Pile pile) {
        int numSticksToTake = random.nextInt(1, 4);
        return new Move(numSticksToTake);
    }
}

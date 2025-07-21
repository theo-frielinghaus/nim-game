package com.theof.nimgame.domain;

public class OptimalMoveStrategy implements ComPlayerStrategy {

    @Override
    public Move makeMove(Pile pile) {
        var sticks = pile.stickCount();

        if (sticks == 1)
            return new Move(PlayerType.COM, 1);
        if (sticks > 1 && sticks <= 4)
            return new Move(PlayerType.COM, sticks - 1);

        var sticksToTake = sticks % 4;

        if (sticksToTake > 0)
            return new Move(PlayerType.COM, sticksToTake);

        return new Move(PlayerType.COM, 1);
    }
}

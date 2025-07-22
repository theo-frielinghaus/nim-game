package com.theof.nimgame.domain;

class OptimalMoveStrategy implements ComPlayerStrategy {

    @Override
    public MoveImpl computeMove(Pile pile) {
        var sticks = pile.stickCount();
        var sticksToTake = 0;

        switch (sticks) {
            case 2,6,10 -> sticksToTake = 1;
            case 3,7,11 -> sticksToTake = 2;
            case 4,8,12 -> sticksToTake = 3;
            case 5,9,13 -> sticksToTake = 1; // Losing position, taking one to stall
        }

        return new MoveImpl(PlayerType.COM, sticksToTake);
    }
}

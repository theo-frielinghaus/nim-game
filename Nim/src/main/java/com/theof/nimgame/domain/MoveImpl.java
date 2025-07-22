package com.theof.nimgame.domain;

record MoveImpl(PlayerType player, int sticksToTake) implements Move{
    public MoveImpl {
        if (sticksToTake < 1 || sticksToTake > 3) throw new IllegalArgumentException("A move must take 1, 2 or 3 sticks from the pile!");
        if (player == null) throw new IllegalArgumentException("A move must have a player who makes the move!");
    }

    @Override
    public Pile execute(Pile pile) {
        return pile.takeSticks(sticksToTake);
    }

    @Override
    public String getPlayerDisplayName() {
        return player.displayName();
    }
}

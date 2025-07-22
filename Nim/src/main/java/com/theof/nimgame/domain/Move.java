package com.theof.nimgame.domain;

public interface Move {
    Pile execute(Pile pile);
    String getPlayerDisplayName();
}

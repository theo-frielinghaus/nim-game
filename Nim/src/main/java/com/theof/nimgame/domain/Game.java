package com.theof.nimgame.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;

@Entity
public class Game extends PanacheEntity {

    @Nonnull
    private String comPlayerStrategyName;

    @Embedded
    private Pile pile;

    public Game() {
    }

    public Game(@Nonnull String comPlayerStrategyName, Pile pile) {
        this.comPlayerStrategyName = comPlayerStrategyName;
        this.pile = pile;
    }

    public ComPlayerStrategy createStrategy() {
        return ComPlayerStrategy.fromType(this.comPlayerStrategyName);
    }

    public Pile getPile() {
        //TODO: check if game is over
        return pile;
    }

    public Pile applyMove(Move move) {
        pile = new Pile(pile.stickCount() - move.sticksToTake());
        return pile;
    }
}

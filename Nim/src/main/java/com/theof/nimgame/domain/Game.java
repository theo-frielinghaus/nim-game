package com.theof.nimgame.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;


@Entity
public class Game extends PanacheEntity {

    @Nonnull
    private String strategyName;

    @Transient
    private ComPlayerStrategy strategy;

    @Embedded
    private Pile pile;

    private boolean isComPlayerFirst;

    public Game() {
    }

    public Game(@Nonnull String strategyName, Pile pile,  boolean isComPlayerFirst) {
        this.strategyName = strategyName;
        this.pile = pile;
        this.isComPlayerFirst = isComPlayerFirst;
    }

    public Pile start() {
        if(!isComPlayerFirst) return pile;
        if(strategy == null) createStrategy();

        pile = strategy.doMove(pile);
        return pile;
    }

    public boolean isComPlayerFirst() {
        return isComPlayerFirst;
    }

    @PrePersist
    @PostLoad
    private void createStrategy() {
        this.strategy = ComPlayerStrategy.fromType(this.strategyName);
    }

}

package com.theof.nimgame.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import static com.theof.nimgame.domain.PlayerType.COM;
import static com.theof.nimgame.domain.PlayerType.HUMAN;

@Entity
public class Game extends PanacheEntity {

    @Nonnull
    private String comPlayerStrategyName;

    @Embedded
    private Pile pile;

    @Enumerated(EnumType.STRING)
    private PlayerType winner;

    public Game() {
    }

    public Game(@Nonnull String comPlayerStrategyName, @Nonnull Pile pile) {
        this.comPlayerStrategyName = comPlayerStrategyName;
        this.pile = pile;
        winner = null;
    }

    public ComPlayerStrategy createStrategy() {
        return ComPlayerStrategy.fromType(this.comPlayerStrategyName);
    }

    public Pile getPile() {
        return pile;
    }

    public Pile applyMove(Move move) {
        if(winner != null) throw new IllegalStateException("Game is already over! No further moves possible.");
        pile = new Pile(pile.stickCount() - move.sticksToTake());
        if (pile.stickCount() == 0) determineWinner(move);
        return pile;
    }

    public PlayerType getWinner() {
        return winner;
    }
    private void determineWinner(Move move) {
        switch (move.player()) {
            case COM -> winner = HUMAN;
            case HUMAN -> winner = COM;
        }
    }

}

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

    public Game(@Nonnull String comPlayerStrategyName, int stickCount) {
        this.comPlayerStrategyName = comPlayerStrategyName;
        this.pile = new Pile(stickCount);
        winner = null;
        ComPlayerStrategy.fromType(this.comPlayerStrategyName); //to validate strategyName before persist
    }

    public int getStickCount() {
        return pile.stickCount();
    }

    public Move createHumanMove(int sticksToTake) {
        return new MoveImpl(HUMAN, sticksToTake);
    }

    public Move createComMove() {
        return ComPlayerStrategy.fromType(comPlayerStrategyName).computeMove(pile);
    }

    public int makeMove(Move move) {
        if (winner != null)
            throw new IllegalStateException("Game is already over! No further moves possible.");

        pile = move.execute(pile);
        if (pile.stickCount() == 0)
            determineWinner(((MoveImpl) move).player());
        return pile.stickCount();
    }

    public PlayerType getWinner() {
        return winner;
    }

    private void determineWinner(PlayerType player) {
        switch (player) {
            case COM -> winner = HUMAN;
            case HUMAN -> winner = COM;
        }
    }

}

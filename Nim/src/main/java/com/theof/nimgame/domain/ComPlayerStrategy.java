package com.theof.nimgame.domain;


 interface ComPlayerStrategy {

    MoveImpl computeMove(Pile pile);

    static ComPlayerStrategy fromType(String strategyName) {
        strategyName = strategyName.toLowerCase();
        return switch (strategyName) {
            case "random" -> new RandomMoveStrategy();
            case "optimal" -> new OptimalMoveStrategy();
            default -> throw new IllegalArgumentException(
                "Unknown strategy: " + strategyName);
        };
    }
}

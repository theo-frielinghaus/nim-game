package com.theof.nimgame.api;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

record SettingsDTO(

    @Schema(description = "The strategy of the computer player to make moves, currently supported: 'random', 'optimal'")
     String comStrategy,

    @Schema(description = "Indicates if human player is starting the game", defaultValue = "true")
     boolean hasHumanPlayerFirstTurn
 ){
    public SettingsDTO(String comStrategy) {
        this(comStrategy, true); // Human player has first turn per default
    }
}

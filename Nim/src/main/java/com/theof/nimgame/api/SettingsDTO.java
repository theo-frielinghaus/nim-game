package com.theof.nimgame.api;

 record SettingsDTO(String comStrategy, boolean hasHumanPlayerFirstTurn){
    public SettingsDTO(String comStrategy) {
        this(comStrategy, true); // Human player has first turn per default
    }
}

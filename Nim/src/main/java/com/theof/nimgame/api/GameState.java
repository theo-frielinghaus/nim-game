package com.theof.nimgame.api;

import java.util.List;

public record GameState(long gameId, int stickCount, List<String> gamelog) {
}

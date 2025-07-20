package com.theof.nimgame.api;

import java.util.List;

public record GameStateDTO(Long gameId, int stickCount, List<String> gamelog) {
}

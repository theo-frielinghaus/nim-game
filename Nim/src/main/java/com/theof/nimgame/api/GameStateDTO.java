package com.theof.nimgame.api;

import com.theof.nimgame.domain.PlayerType;

import java.util.List;

public record GameStateDTO(Long gameId, int stickCount, List<String> gamelog, PlayerType winner) {
}

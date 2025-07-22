package com.theof.nimgame.application;

import com.theof.nimgame.domain.PlayerType;

import java.util.List;

public record GameState(Long gameId, int stickCount, List<String> gamelog, PlayerType winner) {
}

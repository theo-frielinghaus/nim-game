package com.theof.nimgame.api;

import java.util.List;

record GameStateDTO(Long gameId, int stickCount, List<String> gamelog, String winner) {
}

package com.theof.nimgame.api;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;


record GameStateDTO(

    @Schema(description = "Unique ID for the game", example = "123")
    Long gameId,

    @Schema(description = "Number of sticks on the pile", example = "8")
    int stickCount,

    @Schema(description = "List the moves and other info that happend in request (not full game)",
        example = "['Human player made their move and took 3 sticks!', 'Human player made their move and took 1 sticks! Make your Move']")
    List<String> gamelog,

    @Schema(description = "Winner ('Human player', 'Computer player', or null if game is ongoing)", nullable = true)
    String winner
) {
}

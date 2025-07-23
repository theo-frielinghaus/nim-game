package com.theof.nimgame.api;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

record MoveDTO(
    @Schema(
        description = "Number of sticks the player wants to take (must be 1-3 and not exceed remaining sticks).",
        minimum = "1",
        maximum = "3"
    )
    int sticksToTake
) {
}

package com.theof.nimgame.api;

import com.theof.nimgame.application.GameService;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

@Path("/games")
public class GameResource {
    @Inject
    GameService gameService;

    @POST
    public GameStateDTO startGame(SettingsDTO settings) {
        Long gameId = gameService.createGame(settings);
        return gameService.startGame(gameId, true);
    }

    }

    @ServerExceptionMapper({IllegalArgumentException.class})
    public Response handleIllegalArgument(IllegalArgumentException ex) {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity("Invalid arguments: " + ex.getMessage())
            .type(MediaType.TEXT_PLAIN_TYPE)
            .build();
    }
}

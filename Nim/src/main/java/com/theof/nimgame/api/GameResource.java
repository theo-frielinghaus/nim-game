package com.theof.nimgame.api;

import com.theof.nimgame.application.GameService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.util.List;

@Path("/games")
public class GameResource {
    @Inject
    GameService gameService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus REST";
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GameState startGame(NewGameSettings settings) {
        long gameId = gameService.createGame(settings);
        return gameService.startGame(gameId);
    }

    @ServerExceptionMapper({IllegalArgumentException.class})
    public Response handleIllegalArgument(IllegalArgumentException ex) {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity("Invalid arguments: " + ex.getMessage())
            .type(MediaType.TEXT_PLAIN_TYPE)
            .build();
    }
}

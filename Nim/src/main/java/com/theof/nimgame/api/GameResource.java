package com.theof.nimgame.api;

import com.theof.nimgame.application.GameService;
import com.theof.nimgame.application.GameState;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import static com.theof.nimgame.api.GameStateMapper.gameStateDTOFrom;

@Path("/games")
public class GameResource {
    @Inject
    GameService gameService;

    @POST
    @Operation(
        summary = "Create and start a new game",
        description = """
            Creates a game using the provided settings. \n
            Current available computer player strategys are {"comStrategy": "random" || "optimal"}.\n
            Returns the game state at the start of the game, with the gameID that is needed to make moves.\n
            If computer starts the game, they already start with their own move, which will be reflected in game state.
            
            """
    )
    public GameStateDTO startGame(SettingsDTO settings) {
        Long gameId = gameService.createGame(settings.comStrategy());
        GameState gameState = gameService.startGame(gameId, settings.hasHumanPlayerFirstTurn());
        return gameStateDTOFrom(gameState);
    }

    @PUT
    @Path("{gameID}")
    @Operation(
        summary = "Make a Move",
        description = """
            Makes a move with provided number of sticks to take for a running game. \n
            Legal move is an amount of sticks to take between 1-3 that is not bigger than current stick count. \n
            The computer reacts immediately with their own move, included in returned game state. \n
            If a move ends a game, the winner will be included in game state.\n
            The log included in gamestate gives additional information about computer player moves.\n
            
            """
    )
    public GameStateDTO makeMove(@PathParam("gameID") Long gameID, MoveDTO move) {
        GameState gameState = gameService.makeMove(gameID, move.sticksToTake());
        return gameStateDTOFrom(gameState);
    }

    @ServerExceptionMapper({IllegalArgumentException.class})
    public Response handleIllegalArgument(IllegalArgumentException ex) {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity("Invalid arguments: " + ex.getMessage())
            .type(MediaType.TEXT_PLAIN_TYPE)
            .build();
    }

    @ServerExceptionMapper({IllegalStateException.class})
    public Response handleIllegalArgument(IllegalStateException ex) {
        return Response.status(Response.Status.CONFLICT)
            .entity("Invalid arguments: " + ex.getMessage())
            .type(MediaType.TEXT_PLAIN_TYPE)
            .build();
    }
}

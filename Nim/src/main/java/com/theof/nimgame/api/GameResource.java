package com.theof.nimgame.api;

import com.theof.nimgame.application.GameService;
import com.theof.nimgame.application.GameState;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
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
            If computer starts the game, they already start with their own move.
            """
    )
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Game state after creating and starting the game and, if computer player has first turn, after computer move.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GameStateDTO.class)
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Invalid game settings (unknown strategy).",
            content = @Content(
                mediaType = "text/plain"
            )
        )})
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
            """
    )
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Game state after performing the move and computer response.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GameStateDTO.class)
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Invalid move (e.g. too many sticks, more sticks than on pile, etc. ).",
            content = @Content(
                mediaType = "text/plain"
            )
        ),
        @APIResponse(
            responseCode = "409",
            description = "Game is already over.",
            content = @Content(
                mediaType = "text/plain"
            )
        ),
        @APIResponse(
            responseCode = "404",
            description = "Game not found.",
            content = @Content(
                mediaType = "text/plain"
            )
        )
    })
    public GameStateDTO makeMove(@PathParam("gameID") Long gameID, MoveDTO move) {
        GameState gameState = gameService.makeMove(gameID, move.sticksToTake());
        return gameStateDTOFrom(gameState);
    }

    


    @GET
    @Path("{gameID}")
    @Operation(
        summary = "Retrieve game state",
        description = """
            Retrieves the current state of game;
            """
    )
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Current state of game",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GameStateDTO.class)
            )
        ),
        @APIResponse(
            responseCode = "404",
            description = "Game not found.",
            content = @Content(
                mediaType = "text/plain"
            )
        )
    })
    public GameStateDTO getCurrentGameState(@PathParam("gameID") Long gameID) {
        GameState gameState = gameService.getGameState(gameID);
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
            .entity("Illegal state: " + ex.getMessage())
            .type(MediaType.TEXT_PLAIN_TYPE)
            .build();
    }

    @ServerExceptionMapper({NotFoundException.class})
    public Response handleResourceNotFound(NotFoundException ex) {
        return Response.status(Response.Status.NOT_FOUND)
            .entity(ex.getMessage())
            .type(MediaType.TEXT_PLAIN_TYPE)
            .build();
    }
}

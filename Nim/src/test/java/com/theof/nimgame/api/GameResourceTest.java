package com.theof.nimgame.api;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.theof.nimgame.application.GamelogTemplate.GAME_STARTED;
import static com.theof.nimgame.application.GamelogTemplate.HUMAN_PLAYER_TURN;
import static com.theof.nimgame.application.GamelogTemplate.HUMAN_PLAYER_WON;
import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.oneOf;

@QuarkusTest
@TestHTTPEndpoint(GameResource.class)
class GameResourceTest {

    @BeforeAll
    static void setup() {
        enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void new_game_gets_started() {
        SettingsDTO settings = new SettingsDTO("random");
        given()
            .contentType(ContentType.JSON)
            .body(settings)
        .when()
            .post()
        .then()
            .statusCode(200)
            .body("stickCount", is(oneOf(13, 12, 11, 10)))
            .body("gamelog", hasItem(GAME_STARTED.format()))
            .body("winner", is(emptyString()));
    }

    @Test
    void new_game_with_invalid_settings_doesnt_get_created() {
        SettingsDTO settings = new SettingsDTO("llm");
        given()
            .contentType(ContentType.JSON)
            .body(settings)
        .when()
            .post()
        .then()
            .statusCode(400)
            .body(containsString("Unknown strategy: llm"));

    }

    @Test
    void current_game_state_for_specific_game() {

        given()
            .pathParam("gameID", -1)
        .when()
            .get("/{gameID}")
        .then()
            .statusCode(200)
            .body("stickCount", is(13))
            .body("gamelog", is(emptyCollectionOf(List.class)))
            .body("winner", is(emptyString()));
    }

    @Test
    void no_game_state_for_non_existing_game() {

        given()
            .pathParam("gameID", -999L)
        .when()
            .get("/{gameID}")
        .then()
            .statusCode(404)
            .body(containsString("Game with id -999 doesn't exist." ));
    }

    @Test
    void human_player_plays_valid_move() {
        var validMove = new MoveDTO(3);

        given()
            .contentType(ContentType.JSON)
            .body(validMove)
            .pathParam("gameID", -1)
        .when()
            .put("/{gameID}")
        .then()
            .statusCode(200)
            .body("stickCount", is(oneOf(9, 8, 7)))
            .body("gamelog", hasItem(HUMAN_PLAYER_TURN.format(validMove.sticksToTake())))
            .body("winner", is(emptyString()));
    }

    @Test
    void human_player_plays_invalid_move() {
        var invalidMove = new MoveDTO(10);

        given()
            .contentType(ContentType.JSON)
            .body(invalidMove)
            .pathParam("gameID", -2)
        .when()
            .put("/{gameID}")
        .then()
            .statusCode(400)
            .body(containsString("A move must take 1, 2 or 3 sticks from the pile!"));
    }

    @Test
    void after_last_possible_turn_game_is_ended() {
        var validMove = new MoveDTO(1);

        given()
            .contentType(ContentType.JSON)
            .body(validMove)
            .pathParam("gameID", -7)
        .when()
            .put("/{gameID}")
        .then()
            .statusCode(200)
            .body("stickCount", is(0))
            .body("gamelog", hasItem(HUMAN_PLAYER_WON.format()))
            .body("winner", is("Human player"));
    }

    @Test
    void after_game_is_ended_no_turns_possible() {
        var validMove = new MoveDTO(1);

        given()
            .contentType(ContentType.JSON)
            .body(validMove)
            .pathParam("gameID", -8)
        .when()
            .put("/{gameID}")
        .then()
            .statusCode(409)
            .body(containsString("Game is already over! No further moves possible."));
    }

}
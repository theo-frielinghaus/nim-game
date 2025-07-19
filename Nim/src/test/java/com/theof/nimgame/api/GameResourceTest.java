package com.theof.nimgame.api;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.theof.nimgame.application.GamelogTemplate.GAME_STARTED;
import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isOneOf;
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
        NewGameSettings settings = new NewGameSettings("random");
        given()
            .contentType(ContentType.JSON)
            .body(settings)
            .when()
            .post()
            .then()
            .statusCode(200)
            .body("stickCount", is(oneOf(13, 12, 11, 10)))
            .body("gamelog", hasItem(GAME_STARTED.format()));
    }

    @Test
    void new_game_with_invalid_settings_doesnt_get_created() {
        NewGameSettings settings = new NewGameSettings("llm");
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
    void human_player_plays_valid_move() {
    }

    @Test
    void human_player_plays_invalid_move() {
    }

    @Test
    void after_last_possible_turn_game_is_ended() {
    }

}
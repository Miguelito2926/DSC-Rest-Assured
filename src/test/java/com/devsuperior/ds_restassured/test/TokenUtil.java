package com.devsuperior.ds_restassured.test;


import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
public class TokenUtil {

    public static String getAccessToken(String username, String password) {

        Response response = authRequest(username, password);
        JsonPath jsonBody = response.jsonPath();
        return jsonBody.getString("access_token");

    }

    private static Response authRequest(String username, String password) {
            return given()
                    .auth()
                    .preemptive()
                    .basic("myClientId", "myClientSecret")
                    .contentType("appliaction/x-www-form-urlencoded")
                    .formParam("grant_type", "password")
                    .formParam("username", username)
                    .formParam("password", password)
                    .when()
                    .post("/oauth2/token");

    }
}

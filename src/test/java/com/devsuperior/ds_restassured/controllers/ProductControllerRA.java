package com.devsuperior.ds_restassured.controllers;

import com.devsuperior.ds_restassured.test.TokenUtil;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class ProductControllerRA {

    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String clientToken, adminToken, invelidToken;
    private Long existingProductId, nonexistingProductId;
    private String productName;
    private Map<String, Object> postProductInstance;

    @BeforeEach
    public void setUp() {

        baseURI = "http://localhost:8080";

        clientUsername = "maria@gmail.com";
        clientPassword = "123456";
        clientToken = TokenUtil.getAccessToken(clientUsername, clientPassword);

        adminUsername = "alex@gmail.com";
        adminPassword = "123456";
        adminToken = TokenUtil.getAccessToken(adminUsername, adminPassword);

        invelidToken = adminToken + "XPTO";

        productName = "Smart Tv";
        postProductInstance = new HashMap<>();
        postProductInstance.put("name", "Meu Produto");
        postProductInstance.put("description", "Meu Produto inserido teste");
        postProductInstance.put("imgUrl", "http://minhaimage.jpg");
        postProductInstance.put("price", 50.0);

        Map<String, Object> categorie1 = new HashMap<>();
        categorie1.put("id", 1);

        Map<String, Object> categorie2 = new HashMap<>();
        categorie2.put("id", 2);

        List<Map<String, Object>> categories = new ArrayList<>();
        categories.add(categorie1);
        categories.add(categorie2);

        postProductInstance.put("categories", categories);
    }

    @Test
    public void findAllShouldReturnPageProductWhenProductNameIsNotEmpty() {

        existingProductId = 2L;

        given()
                .when()
                .get("/products/{id}", existingProductId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(2))
                .body("name", equalTo("Smart TV"))
                .body("imgUrl",
                        equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg"))
                .body("price", is(2190.0F))
                .body("categories.id", hasItems(2, 3))
                .body("categories.name", hasItems("Eletrônicos", "Computadores"));
    }

    @Test
    public void shouldReturnProductByNameWhenProductExists() {

        existingProductId = 2L;

        given()
                .queryParam("name", productName)
                .when()
                .get("/products")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content[0].id", is(2))
                .body("content[0].name", equalTo("Smart TV"))
                .body("content[0].imgUrl",
                        equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg"))
                .body("content[0].price", is(2190.0F));
    }

    @Test
    public void findAllShouldReturnPageProductsWhenProductNameIsEmpty() {

        given()
                .when()
                .queryParam("size", 25)
                .get("/products")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content.id", hasItems(3, 9))
                .body("content.name", hasItems("Macbook Pro", "PC Gamer Tera"));
    }

    @Test
    public void findAllShouldReturnPageProductWithPriceGreaterThan2000() {

        given()
                .when()
                .queryParam("size", 25)
                .get("/products")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content.findAll { it.price > 2000}.name", hasItems("Smart TV", "PC Gamer Weed"));
    }

    @Test
    public void insertShouldReturnProductCreatedWhenAdminLogged() {
        //convert para json
        JSONObject newProduct = new JSONObject(postProductInstance);
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct)
                .when()
                .get("/product")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", equalTo("Meu Produto"))
                .body("description", equalTo("Meu Produto inserido teste"))
                .body("omgUrl", equalTo("http://minhaimage.jpg"))
                .body("price", equalTo(50.0))
                .body("categories.id", hasItems(1, 2));

    }
}

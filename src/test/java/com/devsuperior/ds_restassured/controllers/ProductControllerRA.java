package com.devsuperior.ds_restassured.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ProductControllerRA {

    private Long existingProductId, nonexistingProductId;
    private String productName;
    private Double productPrice;

    @BeforeEach
    public void setUp() {
        baseURI = "http://localhost:8080";
        productName = "Smart Tv";
        productPrice = 2000.0;
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
                .body("content.id",  hasItems(3, 9))
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
}

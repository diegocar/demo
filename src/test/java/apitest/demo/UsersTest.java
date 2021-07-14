package apitest.demo;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.core.annotation.Order;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


@TestInstance(Lifecycle.PER_CLASS)
public class UsersTest {
    public static Response user;
    public static Response tenUsers;

    @BeforeAll
    public void setup() {

        RestAssured.
                filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        RestAssured.baseURI = "https://api.github.com";
    }

    @Test()
    @Order(1)
    @DisplayName("Verify public users")
    public void testGetGithubUsersResponseTime() {
        user = given().auth()
                .oauth2(System.getenv("ACCESS_TOKEN")).
                        when().
                        get("/users").
                        then(). extract().response();
        assertTrue(user.getTimeIn(TimeUnit.SECONDS) < 5);
    }

    @Test()
    @Order(2)
    @DisplayName("Verify get 10 users")
    public void testGetGithubTenUsers() {
        tenUsers = given().auth()
                .oauth2(System.getenv("ACCESS_TOKEN")).
                        queryParam("per_page","10").
                        when().
                        get("/users").
                        then(). extract().response();
        tenUsers.then().assertThat().body("size()", is(10));
    }

    @Test()
    @Order(3)
    @DisplayName("Verify get 50 users")
    public void testGetGithub50Users() {
        Response fiftyUsers = given().auth()
                .oauth2(System.getenv("ACCESS_TOKEN")).
                        queryParam("per_page","50").
                        when().
                        get("/users").
                        then(). extract().response();
        fiftyUsers.then().assertThat().body("size()", is(50));
    }
}

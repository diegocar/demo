package apitest.demo;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.core.annotation.Order;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;


@TestInstance(Lifecycle.PER_CLASS)
public class RedirectTest {
    public static String urlRedirect = "https://github.com/aperdomob/new-redirect-test";

    @BeforeAll
    public void setup() {

        RestAssured.
                filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test()
    @Order(1)
    @DisplayName("Verify redirect status code 301")
    public void testHeadtGithubRedirect() {
        given().
                redirects().follow(false).
                when().
                head("https://github.com/aperdomob/redirect-test").
                then().
                statusCode(301).
                header("Location", equalTo(urlRedirect));
    }

    @Test()
    @Order(2)
    @DisplayName("Verify redirect status code 200")
    public void testGetGithubRdirect() {
        when().
                get("https://github.com/aperdomob/redirect-test").
                then().
                statusCode(200);
    }
}

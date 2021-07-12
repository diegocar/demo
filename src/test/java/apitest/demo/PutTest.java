package apitest.demo;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(Lifecycle.PER_CLASS)
public class PutTest {

    @Test()
    @DisplayName("Verify Github status code and empty body")
    public void testPutGithubResponse() {
        given().auth().
                oauth2(System.getenv("ACCESS_TOKEN")).
                when().
                put("https://api.github.com/user/following/aperdomob").
                then().assertThat().
                statusCode(204).
                extract().
                body().equals(empty());
    }

    @Test()
    @DisplayName("Verify Github follow list")
    public void testGetGithubFollowingList() {
        given().auth().
                oauth2(System.getenv("ACCESS_TOKEN")).
                when().
                get("https://api.github.com/user/following").
                then().
                body("find { it.login == 'aperdomob' }.login", equalTo("aperdomob"));
    }

    @Test()
    @DisplayName("Verify Github status code and empty body idempotency")
    public void testPutGithubResponseAgain() {
        given().auth().
                oauth2(System.getenv("ACCESS_TOKEN")).
                when().
                put("https://api.github.com/user/following/aperdomob").
                then().assertThat().
                statusCode(204).
                extract().
                body().equals(empty());
    }

    @Test()
    @DisplayName("Verify Github follow list idempotency")
    public void testGetGithubFollowingListAgain() {
        given().auth().
                oauth2(System.getenv("ACCESS_TOKEN")).
                when().
                get("https://api.github.com/user/following").
                then().
                body("find { it.login == 'aperdomob' }.login", equalTo("aperdomob"));
    }

}

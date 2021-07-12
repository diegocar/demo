package apitest.demo;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.core.annotation.Order;

import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


@TestInstance(Lifecycle.PER_CLASS)
public class IssueTest {
    public static Response user;
    public static Response repositories;
    public static Response issueRequest;

    @BeforeAll
    public void setup() {

        RestAssured.
                filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        RestAssured.baseURI = "https://api.github.com";
    }

    @Test()
    @Order(1)
    @DisplayName("Verify public repos")
    public void testGetGithubUser() {
        user = given().auth()
                .oauth2(System.getenv("ACCESS_TOKEN")).
                when().
                get("/user").
                then(). extract().response();

        user.then().assertThat().
                body("public_repos", not(0));
    }

    @Test()
    @Order(2)
    @DisplayName("Verify a public repository")
    public void testGetGithubUserRepositories() {
        String reposPath= user.getBody().path("repos_url").toString();

        repositories = given().auth()
                .oauth2(System.getenv("ACCESS_TOKEN")).
                when().
                get(reposPath).
                then().extract().response();

        repositories.then().assertThat().
                body("find { it.name == 'demo' }.full_name", equalTo("diegocar/demo"));
    }

    @Test()
    @Order(3)
    @DisplayName("Post a issue with no body")
    public void postGithubIssueTest() {
        String userName= user.getBody().path("login");
        String repoName= repositories.getBody().path("find { it.name == 'demo' }.name");

//        JSONObject issue = new JSONObject();
//        issue.append("title","Issue test");
        Map<String,String> issue = new HashMap<String,String>();
        issue.put("title", "Issue test");

        issueRequest = given().auth()
                .oauth2(System.getenv("ACCESS_TOKEN")).
                body(issue).
                when().
                post("/repos/"+userName+"/"+repoName+"/issues").
                then().extract().response();

        issueRequest.then().assertThat().
                body("body", nullValue()).
                body("title", equalTo("Issue test")).
                statusCode(201);
    }

    @AfterAll()
    @Test()
    @Order(4)
    @DisplayName("Pacth a issue with a body")
    public void testIssueBodyPatch() {
        String userName= user.getBody().path("login");
        String repoName= repositories.getBody().path("find { it.name == 'demo' }.name");

        Map<String,String> issue = new HashMap<String,String>();
        issue.put("body", "Issue body");

        given().auth()
                .oauth2(System.getenv("ACCESS_TOKEN")).
                body(issue).
                when().
                patch("/repos/"+userName+"/"+repoName+"/issues/30").
                then().
                assertThat().
                body("body", equalTo("Issue body")).
                body("title", equalTo("Issue test"));
    }
}

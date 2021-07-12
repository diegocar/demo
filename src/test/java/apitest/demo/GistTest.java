package apitest.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.core.annotation.Order;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(Lifecycle.PER_CLASS)
public class GistTest {

    public static Response gistPetition;
    public static Response repositories;

    @BeforeAll
    public void setup() {

        RestAssured.
                filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        RestAssured.baseURI = "https://api.github.com";
    }

    @Test()
    @Order(1)
    @DisplayName("Post a Gist")
    public void testPostGithubGist() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode gist = mapper.createObjectNode();

        gist.put("public",true);
        gist.put("description","promise example");

        ObjectNode file = mapper.createObjectNode();
        file.put("content", "jsCode");

        ObjectNode files = mapper.createObjectNode();
        files.set("promise.js", file);

        gist.set("files", files);

        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(gist);
        System.out.println(jsonString);

        gistPetition = given().auth()
                .oauth2(System.getenv("ACCESS_TOKEN")).
                        body(gist).
                        when().
                        post("/gists").
                        then().extract().response();

        gistPetition.then().assertThat().
                statusCode(201).
                body("description", equalTo("promise example"));
    }

    @Test()
    @Order(2)
    @DisplayName("Get the Gist created")
    public void testGetGithubGist(){
        String gistPath= gistPetition.getBody().path("url").toString();

        given().auth()
                .oauth2(System.getenv("ACCESS_TOKEN")).
                when().
                get(gistPath).
                then().
                statusCode(200);
    }

    @Test()
    @Order(3)
    @DisplayName("Delete the Gist created")
    public void testDeleteGithubGist(){
        String gistPath= gistPetition.getBody().path("url").toString();

        given().auth()
                .oauth2(System.getenv("ACCESS_TOKEN")).
                when().
                delete(gistPath).
                then().
                statusCode(204);
    }

    @Test()
    @Order(4)
    @DisplayName("Get the Gist deleted 404")
    public void testGetGithubGistDeleted(){
        String gistPath= gistPetition.getBody().path("url").toString();

        given().auth()
                .oauth2(System.getenv("ACCESS_TOKEN")).
                when().
                get(gistPath).
                then().
                statusCode(404);
    }
}

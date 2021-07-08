package apitest.demo;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static io.restassured.RestAssured.when;


@TestInstance(Lifecycle.PER_CLASS)
public class DemoApplicationTests {

	@Test
	public void testEquals() {
		assertEquals(2, 2);
	}

	@BeforeAll
	public void setup() {

		RestAssured.
				filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

		RestAssured.baseURI = "https://httpbin.org/";
	}

	@Test()
	@DisplayName("verify status code")
	public void testGetStatusCode() {
		when().
				get("/ip").
				then().
				statusCode(200);
	}

	@Test()
	@DisplayName("verify origin value")
	public void testGetOriginInBody() {
		when().
				get("/ip").
				then().
				statusCode(200).
				body("origin", notNullValue());
	}

	@Test()
	@DisplayName("get with query parameters")
	public void testGetQueryParameters() {
		given()
				.queryParam("myInitials", "DC").
				when().
				get("/get").
				then().
				statusCode(200).
				body("args.myInitials", equalTo("DC"));
	}

	@Test()
	@DisplayName("Head test")
	public void testHead() {
		when().
				head("/get").
				then().
				statusCode(200).
				assertThat().header("Content-Type", "application/json");
	}

	@Test()
	@DisplayName("Patch test")
	public void testPatch() {
		String requestBody = "{\n" +
				"  \"Color\": \"Black\" \n}";
		given().
				header("Content-type", "application/json").
				and().
				body(requestBody).
				when().
				patch("/patch").
				then().
				statusCode(200).
				body("json.Color", equalTo("Black"));
	}

	@Test()
	@DisplayName("Put test")
	public void testPut() {
		String requestBody = "{\n" +
				"  \"Color\": \"White\" \n}";
		given().
				header("Content-type", "application/json").
				and().
				body(requestBody).
				when().
				put("/put").
				then().
				statusCode(200).
				body("json.Color", equalTo("White"));
	}

	@Test()
	@DisplayName("Delete test")
	public void testDelete() {
		String requestBody = "{\n" +
				"  \"Color\": \"Red\" \n}";
		given().
				header("Content-type", "application/json").
				and().
				body(requestBody).
				when().
				delete("/delete").
				then().
				statusCode(200).
				body("json.Color", equalTo("Red"));
	}

	@Test()
	@DisplayName("verify Github login")
	public void testGetGithubLogIn() {
		given().auth()
				.oauth2(System.getenv("ACCESS_TOKEN")).
				when().
				get("https://api.github.com/repos/diegocar/demo").
				then().
				assertThat().
				body("permissions", notNullValue());
	}
}

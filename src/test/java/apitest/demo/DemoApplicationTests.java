package apitest.demo;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
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
}

package apitest.demo;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


@TestInstance(Lifecycle.PER_CLASS)
public class RepositoriesTest {
    public static Response aperdomobUser;
    public static Response repositories;
    public static Response repositoryFiles;

    @BeforeAll
    public void setup() {

        RestAssured.
                filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        RestAssured.baseURI = "https://api.github.com/users/aperdomob";
        aperdomobUser = given().auth().
                oauth2(System.getenv("ACCESS_TOKEN")).
                when().
                get(RestAssured.baseURI).
                then().extract().response();
    }

    @Test()
    @DisplayName("Verify Github name, company and user's location")
    public void testGetGithubValidations() {
        aperdomobUser.then().assertThat().
                body("name", equalTo("Alejandro Perdomo")).
                body("company", equalTo("Perficient Latam")).
                body("location", equalTo("Colombia"));
    }

    @Test()
    @DisplayName("Verify Github jasmine-awesome-report repository")
    public void testGetGithubRepository() {
        String reposPath= aperdomobUser.getBody().path("repos_url").toString();

        repositories = given().auth().
                oauth2(System.getenv("ACCESS_TOKEN")).
                when().
                get(reposPath).
                then().extract().response();

        repositories.then().
                assertThat().
                body("find { it.name == 'jasmine-awesome-report' }.full_name", equalTo("aperdomob/jasmine-awesome-report")).
                body("find { it.name == 'jasmine-awesome-report' }.private", equalTo(false)).
                body("find { it.name == 'jasmine-awesome-report' }.description", equalTo("An awesome html report for Jasmine"));
    }

    @Test()
    @DisplayName("Download jasmine-awesome-report repository zip")
    public void testDownloadGithubRepository() {
        String svnUrl= repositories.getBody().path("find { it.name == 'jasmine-awesome-report' }.svn_url").toString();
        String defaultBranch= repositories.getBody().path("find { it.name == 'jasmine-awesome-report' }.default_branch").toString();
        String downloadUrl= svnUrl+"/archive/"+defaultBranch+".zip";
        given().auth().
                oauth2(System.getenv("ACCESS_TOKEN")).
                when().
                get(downloadUrl).
                then().
                assertThat().
                contentType("application/zip");
    }

    @Test()
    @DisplayName("Verify jasmine-awesome-report README.md values")
    public void testGetGithubReadMe() {
        String url= repositories.getBody().path("find { it.name == 'jasmine-awesome-report' }.url").toString();

        repositoryFiles = given().auth().
                oauth2(System.getenv("ACCESS_TOKEN")).
                when().
                get(url+"/contents").
                then().extract().response();

        repositoryFiles.then().
                assertThat().
                body("find { it.name == 'README.md' }.name", equalTo("README.md")).
                body("find { it.name == 'README.md' }.path", equalTo("README.md")).
                body("find { it.name == 'README.md' }.sha", equalTo("1eb7c4c6f8746fcb3d8767eca780d4f6c393c484"));
    }


    @Test()
    @DisplayName("Download jasmine-awesome-report README.md")
    public void testGetGithubReadMeFile() throws Exception{
        String readMeDownloadURL= repositoryFiles.getBody().path("find { it.name == 'README.md' }.download_url").toString();
        String expectedMd5 = "97ee7616a991aa6535f24053957596b1";
        String file = 	given().auth().
                oauth2(System.getenv("ACCESS_TOKEN")).
                when().
                get(readMeDownloadURL).
                then().extract().asString();
        Path path = Paths.get("README.md");
        Files.write(path, file.getBytes());
        Assertions.assertEquals(getMD5ChecksumString("README.md"), expectedMd5);
    }

    private static byte[] createChecksum(String fileName) throws Exception {
        InputStream fis = new FileInputStream(fileName);
        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;
        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);
        fis.close();
        return complete.digest();
    }

    private static String getMD5ChecksumString(String fileName) throws Exception {
        byte[] b = createChecksum(fileName);
        StringBuilder result = new StringBuilder();
        for (byte unByte : b) {
            result.append(Integer.toString((unByte & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }
}

package apitests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class APITests {

    static String loginToken;

    @BeforeTest
    public void loginTest() throws JsonProcessingException {
        // create new LoginPOJO class object named login
        LoginPOJO login = new LoginPOJO();

        // set the login credentials to our login object
        login.setUsernameOrEmail("testuser2205");
        login.setPassword("Test123");

        // Convert pojo object to json using Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        String convertedJson = objectMapper.writeValueAsString(login);
        System.out.println("CONVERTED JSON IS: ");
        System.out.println(convertedJson);

        baseURI = "http://training.skillo-bg.com:3100";

        Response response = given()
                .header("Content-Type", "application/json")
                .body(convertedJson)
                .when()
                .post("/users/login");
        response
                .then()
                .statusCode(201);

        // convert the response body json into a string
        String loginResponseBody = response.getBody().asString();

        loginToken = JsonPath.parse(loginResponseBody).read("$.token");

    }

    @Test
    public void likePost() {
        // create an object of ActionsPOJO class and add value for the fields
        ActionsPOJO likePost = new ActionsPOJO();
        likePost.setAction("likePost");

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + loginToken)
                .body(likePost)
                .when()
                .patch("/posts/4709")
                .then()
                .body("post.id", equalTo(4709))
                .log()
                .all();

    }

    @Test
    public void commentPost() {
        ActionsPOJO commentPost = new ActionsPOJO();
        commentPost.setContent("Comment Test123!");

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + loginToken)
                .body(commentPost)
                .when()
                .post("/posts/4709/comment")
                .then()
                .body("content", equalTo("Comment Test123!"))
                .log()
                .all()
                .statusCode(201);
    }
    @Test
    public void deleteComment() {
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + loginToken)
                .when()
                .delete("/posts/4709/comments/1343")
                .then()
                .log()
                .all()
                .statusCode(200);




    }

}
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.User;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class UserLoginTest {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    private static final String CREATE_USER_PATH = "/api/auth/register";
    private static final String DELETE_USER_PATH = "/api/auth/user";
    private static final String LOGIN_USER_PATH = "/api/auth/login";

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    public void loginExistingUserTest() {
        User user = new User("user11@email.com", "password", "user11");
        createUser(user);

        Response response = given().header("Content-type", "application/json")
                .body(user)
                .post(LOGIN_USER_PATH);

        response.then()
                .assertThat().body("success", equalTo(true))
                .assertThat().body("user.email", equalTo(user.getEmail()))
                .assertThat().body("accessToken", notNullValue());
        response.then().statusCode(200);

        String bearerToken = response.jsonPath().get("accessToken");
        deleteUser(bearerToken);
    }

    @Test
    public void loginWIthInvalidCredentialsTest() {
        User user = new User("user12@email.com", "password", "user12");
        Response response = given().header("Content-type", "application/json")
                .body(user)
                .post(LOGIN_USER_PATH);

        response.then()
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("email or password are incorrect"));
        response.then().statusCode(401);
    }

    private Response createUser(User user) {
        return given().header("Content-type", "application/json")
                .body(user)
                .post(CREATE_USER_PATH);
    }

    private void deleteUser(String token) {
        Response response = given().headers("Authorization", token)
                .delete(DELETE_USER_PATH);
        response.then().statusCode(202);
    }
}

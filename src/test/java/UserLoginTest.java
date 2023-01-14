import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.User;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class UserLoginTest {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    public void loginExistingUserTest() {
        User user = new User("user11@email.com", "password", "user11");
        UserService.createUser(user);

        Response response = UserService.loginUser(user);

        response.then()
                .assertThat().body("success", equalTo(true))
                .assertThat().body("user.email", equalTo(user.getEmail()))
                .assertThat().body("accessToken", notNullValue());
        response.then().statusCode(200);

        String bearerToken = response.jsonPath().get("accessToken");
        UserService.deleteUser(bearerToken);
    }

    @Test
    public void loginWIthInvalidCredentialsTest() {
        User user = new User("user12@email.com", "password", "user12");
        Response response = UserService.loginUser(user);

        response.then()
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("email or password are incorrect"));
        response.then().statusCode(401);
    }
}

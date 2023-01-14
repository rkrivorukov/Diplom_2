import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.User;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class UserCreateTest {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    public void createUniqueUserTest() {
        User user = new User("user11@email.com", "password", "user11");
        Response response = UserService.createUser(user);

        response.then()
                .assertThat().body("success", equalTo(true))
                .assertThat().body("user.email", equalTo(user.getEmail()))
                .assertThat().body("accessToken", notNullValue());
        response.then().statusCode(200);

        String bearerToken = response.jsonPath().get("accessToken");
        UserService.deleteUser(bearerToken);
    }

    @Test
    public void createUserAlreadyExistsTest() {
        User user = new User("user11@email.com", "password", "user11");
        Response response = UserService.createUser(user);
        String bearerToken = response.jsonPath().get("accessToken");

        response = UserService.createUser(user);

        response.then()
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("User already exists"))
                .statusCode(403);

        UserService.deleteUser(bearerToken);
    }

    @Test
    public void createUserEmptyName() {
        User user = new User("user11@email.com", "password", null);
        Response response = UserService.createUser(user);

        response.then()
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("Email, password and name are required fields"))
                .statusCode(403);
    }

}

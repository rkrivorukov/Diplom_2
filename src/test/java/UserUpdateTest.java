import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.User;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class UserUpdateTest {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    public void updateUserAuthorized() {
        User user = new User("user11@email.com", "password", "user11");
        UserService.createUser(user);
        Response response = UserService.loginUser(user);

        String accessToken = response.jsonPath().getString("accessToken");

        User user2 = new User("user13@email.com", "password13", "user13");
        response = UserService.updateUser(accessToken, user2);


        response.then().assertThat().body("success", equalTo(true));
        response.then().statusCode(200);

        response = UserService.getInfo(accessToken);
        response.then()
                .assertThat().body("success", equalTo(true))
                .assertThat().body("user.email", equalTo(user2.getEmail()))
                .assertThat().body("user.name", equalTo(user2.getName()));
        response.then().statusCode(200);

        UserService.updateUser(accessToken, user);
        UserService.deleteUser(accessToken);
    }

    @Test
    public void updateUserUnauthorized() {
        User user = new User("user11@email.com", "password", "user11");
        Response response = UserService.createUser(user);
        String accessToken = response.jsonPath().getString("accessToken");

        response = UserService.updateUser("invalid_bearer_token", user);

        response.then()
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("You should be authorised"));
        response.then().statusCode(401);
        UserService.deleteUser(accessToken);
    }
}

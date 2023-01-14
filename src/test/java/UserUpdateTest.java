import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.UserDto;
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
        UserDto userDto = new UserDto("user11@email.com", "password", "user11");
        UserService.createUser(userDto);
        Response response = UserService.loginUser(userDto);

        String accessToken = response.jsonPath().getString("accessToken");

        UserDto userDto2 = new UserDto("user13@email.com", "password13", "user13");
        response = UserService.updateUser(accessToken, userDto2);


        response.then().assertThat().body("success", equalTo(true));
        response.then().statusCode(200);

        response = UserService.getInfo(accessToken);
        response.then()
                .assertThat().body("success", equalTo(true))
                .assertThat().body("user.email", equalTo(userDto2.getEmail()))
                .assertThat().body("user.name", equalTo(userDto2.getName()));
        response.then().statusCode(200);

        UserService.updateUser(accessToken, userDto);
        UserService.deleteUser(accessToken);
    }

    @Test
    public void updateUserUnauthorized() {
        UserDto userDto = new UserDto("user11@email.com", "password", "user11");
        Response response = UserService.createUser(userDto);
        String accessToken = response.jsonPath().getString("accessToken");

        response = UserService.updateUser("invalid_bearer_token", userDto);

        response.then()
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("You should be authorised"));
        response.then().statusCode(401);
        UserService.deleteUser(accessToken);
    }
}

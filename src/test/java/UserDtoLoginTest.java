import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.UserDto;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class UserDtoLoginTest {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    public void loginExistingUserTest() {
        UserDto userDto = new UserDto("user11@email.com", "password", "user11");
        UserService.createUser(userDto);

        Response response = UserService.loginUser(userDto);

        response.then()
                .assertThat().body("success", equalTo(true))
                .assertThat().body("user.email", equalTo(userDto.getEmail()))
                .assertThat().body("accessToken", notNullValue());
        response.then().statusCode(200);

        String bearerToken = response.jsonPath().get("accessToken");
        UserService.deleteUser(bearerToken);
    }

    @Test
    public void loginWIthInvalidCredentialsTest() {
        UserDto userDto = new UserDto("user12@email.com", "password", "user12");
        Response response = UserService.loginUser(userDto);

        response.then()
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("email or password are incorrect"));
        response.then().statusCode(401);
    }
}

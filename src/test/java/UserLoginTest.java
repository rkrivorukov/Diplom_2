import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.UserDto;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class UserLoginTest {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    private String accessToken = "";

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @After
    public void tearDown() {
        UserService.deleteUser(accessToken);
    }

    @Test
    public void loginExistingUserTest() {
        UserDto userDto = new UserDto("user11@email.com", "password", "user11");
        UserService.createUser(userDto);

        Response response = UserService.loginUser(userDto);

        response.then()
                .assertThat().body("success", equalTo(true))
                .assertThat().body("user.email", equalTo(userDto.getEmail()))
                .assertThat().body("accessToken", notNullValue())
                .assertThat().statusCode(200);

        accessToken = response.jsonPath().get("accessToken");
    }

    @Test
    public void loginWIthInvalidCredentialsTest() {
        UserDto userDto = new UserDto("user12@email.com", "password", "user12");
        Response response = UserService.loginUser(userDto);

        response.then()
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("email or password are incorrect"))
                .assertThat().statusCode(401);
    }
}

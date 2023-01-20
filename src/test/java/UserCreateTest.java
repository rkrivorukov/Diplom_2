import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.UserDto;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class UserCreateTest {

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
    public void createUniqueUserTest() {
        UserDto userDto = new UserDto("user11@email.com", "password", "user11");
        Response response = UserService.createUser(userDto);

        response.then()
                .assertThat().body("success", equalTo(true))
                .assertThat().body("user.email", equalTo(userDto.getEmail()))
                .assertThat().body("accessToken", notNullValue())
                .assertThat().statusCode(200);

        accessToken = response.jsonPath().get("accessToken");
    }

    @Test
    public void createUserAlreadyExistsTest() {
        UserDto userDto = new UserDto("user11@email.com", "password", "user11");
        Response response = UserService.createUser(userDto);
        accessToken = response.jsonPath().get("accessToken");

        response = UserService.createUser(userDto);

        response.then()
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("User already exists"))
                .assertThat().statusCode(403);
    }

    @Test
    public void createUserEmptyName() {
        UserDto userDto = new UserDto("user11@email.com", "password", null);
        Response response = UserService.createUser(userDto);

        response.then()
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("Email, password and name are required fields"))
                .assertThat().statusCode(403);
    }

}

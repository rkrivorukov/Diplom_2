import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.UserDto;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class UserDtoCreateTest {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    public void createUniqueUserTest() {
        UserDto userDto = new UserDto("user11@email.com", "password", "user11");
        Response response = UserService.createUser(userDto);

        response.then()
                .assertThat().body("success", equalTo(true))
                .assertThat().body("user.email", equalTo(userDto.getEmail()))
                .assertThat().body("accessToken", notNullValue());
        response.then().statusCode(200);

        String bearerToken = response.jsonPath().get("accessToken");
        UserService.deleteUser(bearerToken);
    }

    @Test
    public void createUserAlreadyExistsTest() {
        UserDto userDto = new UserDto("user11@email.com", "password", "user11");
        Response response = UserService.createUser(userDto);
        String bearerToken = response.jsonPath().get("accessToken");

        response = UserService.createUser(userDto);

        response.then()
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("User already exists"))
                .statusCode(403);

        UserService.deleteUser(bearerToken);
    }

    @Test
    public void createUserEmptyName() {
        UserDto userDto = new UserDto("user11@email.com", "password", null);
        Response response = UserService.createUser(userDto);

        response.then()
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("Email, password and name are required fields"))
                .statusCode(403);
    }

}

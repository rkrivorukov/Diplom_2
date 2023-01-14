import io.restassured.response.Response;
import model.UserDto;

import static io.restassured.RestAssured.given;

public class UserService {
    private static final String CREATE_USER_PATH = "/api/auth/register";
    private static final String DELETE_USER_PATH = "/api/auth/user";
    private static final String LOGIN_USER_PATH = "/api/auth/login";
    private static final String UPDATE_USER_PATH = "/api/auth/user";
    private static final String USER_INFO_PATH = "/api/auth/user";

    public static Response deleteUser(String token) {
        return given().headers("Authorization", token)
                .delete(DELETE_USER_PATH);
    }

    public static Response createUser(UserDto userDto) {
        return given().header("Content-type", "application/json")
                .body(userDto)
                .post(CREATE_USER_PATH);
    }

    public static Response loginUser(UserDto userDto) {
        return given().header("Content-type", "application/json")
                .body(userDto)
                .post(LOGIN_USER_PATH);
    }

    public static Response updateUser(String token, UserDto userDto) {
        return given().headers("Authorization", token, "Content-type", "application/json")
                .body(userDto)
                .patch(UPDATE_USER_PATH);
    }

    public static Response getInfo(String token) {
        return given().headers("Authorization", token)
                .get(USER_INFO_PATH);
    }
}

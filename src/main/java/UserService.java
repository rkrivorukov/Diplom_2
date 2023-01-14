import io.restassured.response.Response;
import model.User;

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

    public static Response createUser(User user) {
        return given().header("Content-type", "application/json")
                .body(user)
                .post(CREATE_USER_PATH);
    }

    public static Response loginUser(User user) {
        return given().header("Content-type", "application/json")
                .body(user)
                .post(LOGIN_USER_PATH);
    }

    public static Response updateUser(String token, User user) {
        return given().headers("Authorization", token, "Content-type", "application/json")
                .body(user)
                .patch(UPDATE_USER_PATH);
    }

    public static Response getInfo(String token) {
        return given().headers("Authorization", token)
                .get(USER_INFO_PATH);
    }
}

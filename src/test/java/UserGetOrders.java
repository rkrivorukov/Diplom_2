import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.OrderDto;
import model.UserDto;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class UserGetOrders {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    public void getOrdersAuthorized() {
        UserDto userDto = new UserDto("user11@email.com", "password", "user11");
        UserService.createUser(userDto);
        Response response = UserService.loginUser(userDto);
        String accessToken = response.jsonPath().getString("accessToken");

        OrderDto orderDto = new OrderDto(List.of("61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa70"));
        given().headers("Content-type", "application/json", "Authorization", accessToken)
                .body(orderDto)
                .post(OrderService.CREATE_ORDER_PATH);

        response = UserService.getOrders(accessToken);
        System.out.println(response.getBody().asString());

        response.then()
                .assertThat().body("success", equalTo(true))
                .assertThat().body("orders.total", notNullValue())
                .assertThat().body("orders.totalToday", notNullValue());
        response.then().statusCode(200);
        UserService.deleteUser(accessToken);
    }

    @Test
    public void getOrdersUnuthorized() {

        OrderDto orderDto = new OrderDto(List.of("61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa70"));
        OrderService.createOrder(orderDto);

        Response response = given().get(UserService.USER_ORDERS_PATH);
        System.out.println(response.getBody().asString());

        response.then()
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("You should be authorised"));
        response.then().statusCode(401);
    }
}

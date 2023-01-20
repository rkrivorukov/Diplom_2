import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.OrderDto;
import model.UserDto;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderCreateTest {
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
    public void createOrderAuthorized() {
        UserDto userDto = new UserDto("user11@email.com", "password", "user11");
        UserService.createUser(userDto);
        Response response = UserService.loginUser(userDto);
        accessToken = response.jsonPath().getString("accessToken");

        OrderDto orderDto = new OrderDto(List.of("61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa70"));
        response = given().headers("Content-type", "application/json", "Authorization", accessToken)
                .body(orderDto)
                .post(OrderService.CREATE_ORDER_PATH);

        response.then()
                .assertThat().body("success", equalTo(true))
                .assertThat().body("name", notNullValue())
                .assertThat().body("order.owner.name", equalTo(userDto.getName()))
                .assertThat().body("order.ingredients._id", equalTo(orderDto.getIngredients()))
                .assertThat().body("order.number", notNullValue())
                .assertThat().statusCode(200);
    }

    @Test
    public void createOrderUnauthorized() {
        OrderDto orderDto = new OrderDto(List.of("61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa70"));
        Response response = OrderService.createOrder(orderDto);

        response.then()
                .assertThat().body("success", equalTo(true))
                .assertThat().body("name", notNullValue())
                .assertThat().body("order.number", notNullValue())
                .assertThat().statusCode(200);
    }

    @Test
    public void createOrderNoIngredients() {
        OrderDto orderDto = new OrderDto();
        Response response = OrderService.createOrder(orderDto);

        response.then()
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("Ingredient ids must be provided"))
                .assertThat().statusCode(400);
    }

    @Test
    public void createOrderInvalidIngredient() {
        OrderDto orderDto = new OrderDto(List.of("61c0c5a71d1df82001bdaaa6f", "61c0c5a71d1f82001bdaaa70"));
        Response response = OrderService.createOrder(orderDto);

        response.then().statusCode(500);
    }
}

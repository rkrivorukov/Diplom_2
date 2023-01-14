import io.restassured.response.Response;
import model.OrderDto;

import static io.restassured.RestAssured.given;

public class OrderService {

    public static final String CREATE_ORDER_PATH = "/api/orders";

    public static Response createOrder(OrderDto order) {
        return given().header("Content-type", "application/json")
                .body(order)
                .post(CREATE_ORDER_PATH);
    }
}

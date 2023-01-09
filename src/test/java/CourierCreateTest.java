import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import jdk.jfr.Description;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class CourierCreateTest {
    static private String loginTest = RandomStringUtils.randomAlphabetic(10);
    static private String passwordTest = "1209209309";
    static private String firstNameTest = "Andy";
    static private String endPointCreate = "/api/v1/courier";
    static private String endPointLogin = "/api/v1/courier/login";
    static private String endPointDelete = "/api/v1/courier/";

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
    }

    @After
    public void tearDown() {
        CourierProfile courierDelete  = new CourierProfile(loginTest, passwordTest);
        String response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courierDelete)
                .when()
                .post(endPointLogin)
                .asString();

        JsonPath jsonPath = new JsonPath(response);
        String userId = jsonPath.getString("id");
        delete(endPointDelete + userId);
    }

    @Test
    @DisplayName("Создание нового курьера с правильными данными")
    @Description("/api/v1/courier post: login, password, firstName")
    public void createNewCourierAndCheckResponse(){
        CourierProfile courierCreate  = new CourierProfile(loginTest, passwordTest, firstNameTest);
// Проверяем, что курьер создан:
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courierCreate)
                .when()
                .post(endPointCreate)
                .then().assertThat().statusCode(201)
                .and()
                .body("ok", equalTo(true));
    }
    @Test
    @DisplayName("Создание нового курьера без обязательного поля firstName")
    @Description("/api/v1/courier post: login, password")
    public  void createCourierWithoutFirstName() {
        CourierProfile courierCreate  = new CourierProfile(loginTest, passwordTest);
        given()
                .body(courierCreate)
                .when()
                .post(endPointCreate)
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
    @Test
    @DisplayName("Создание двух одинаковых курьеров")
    @Description("/api/v1/courier post: login, password, firstName")
    public void createCourierWithSameLoginAndCheckResponse(){
        CourierProfile courierCreate  = new CourierProfile(loginTest, passwordTest, firstNameTest);
// Проверяем, что курьер создан:
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courierCreate)
                .when()
                .post(endPointCreate)
                .then().assertThat().statusCode(201)
                .and()
                .body("ok", equalTo(true));
// Проверяем, что нельзя создать такого же курьера:
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courierCreate)
                .when()
                .post(endPointCreate)
                .then().assertThat().statusCode(409)
                .and()
                .body("message", equalTo("Этот логин уже используется")); // Actual: Этот логин уже используется. Попробуйте другой.
    }
}

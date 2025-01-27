import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ApiTesting {

    @DataProvider(name = "isbnData")
    public Object[][] isbnDataProvider() {
        return new Object[][]{

                {"9781449331818"},
                {"9781449337711"},
                {"9781449365035"},
                {"9781491904244"}
        };
    }

    @Test(dataProvider = "isbnData")
    public void isbnDataTest(String isbnNumber) {
        RestAssured.baseURI = "https://bookstore.toolsqa.com";
        Response response = RestAssured
                .given()
                .queryParam("ISBN", isbnNumber)
                .when()
                .get("BookStore/v1/Book")
                .then()
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200, "Status code must be 200");
        String isbn = response.jsonPath().getString("isbn");
        Assert.assertEquals(isbn, isbnNumber, "ISBN number is not equals");
    }
    @Test
    public void regUserValidScenario(){
        RestAssured.baseURI = "https://bookstore.toolsqa.com";
        Faker faker = new Faker();
        LombokData userData = new LombokData();
        userData.setUserName(faker.name().firstName());
        userData.setPassword("Beka123!");

        Response response = RestAssured
                .given()
                .accept(" application/json")
                .contentType(" application/json")
                .body(userData)
                .when()
                .post("Account/v1/User")
                .then()
                .log().all()
                .extract().response();
        Assert.assertEquals(response.getStatusCode(),201, "Status Code Must Be 201");
        Assert.assertNotNull(response.jsonPath().getString("userID"), "No userid");
    }
    @Test
    public void regUserInvalidScenario(){
        RestAssured.baseURI = "https://bookstore.toolsqa.com";
        Faker faker = new Faker();
        LombokData userData = new LombokData();
        userData.setUserName(faker.name().firstName());
        userData.setPassword("beka");

        Response response = RestAssured
                .given()
                .accept(" application/json")
                .contentType(" application/json")
                .body(userData)
                .when()
                .post("Account/v1/User")
                .then()
                .log().all()
                .extract().response();
        Assert.assertEquals(response.getStatusCode(), 400, "Status code should be 200");
        String errorMessage = "Passwords must have at least one non alphanumeric character, one digit ('0'-'9'), one uppercase ('A'-'Z'), one lowercase ('a'-'z'), one special character and Password must be eight characters or longer.";
        String getErrorMessage = response.jsonPath().getString("message");
        Assert.assertEquals(getErrorMessage, errorMessage);
    }
}
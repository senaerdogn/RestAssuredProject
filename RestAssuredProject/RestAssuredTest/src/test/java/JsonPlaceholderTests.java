import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import io.restassured.http.ContentType;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class JsonPlaceholderTests {

    // **Status Code Testi:** API'nin başarılı bir şekilde `200 OK` durum kodu döndürüp döndürmediğini kontrol edin.
    @Test
    public void StatusCodeTests(){
        given()
                .when()
                    .get("https://jsonplaceholder.typicode.com/posts")
                .then()
                    .statusCode(200)
                    .log().all();
    }

    /* **JSON Yapısı Doğrulama:** JSON çıktısının bir dizi (array) içerdiğini ve her bir gönderinin belirli
    alanlara (örneğin, `userId`, `id`, `title`, `body`) sahip olduğunu doğrulayın. */
    @Test
    public void validateJsonStructure() {
        given()
                .contentType(ContentType.JSON)
                .when()
                    .get("https://jsonplaceholder.typicode.com/posts")
                .then()
                    .log().all()
                    .assertThat()
                    .contentType(ContentType.JSON) //Yanıtın JSON biçiminde olduğunu doğrular.
                    .body("$", not(empty())) //JSON dizisinin boş olmadığını doğrular.
                    .body("userId", everyItem(notNullValue())) //Her objenin bir userId alanına sahip olduğunu doğrular.
                    .body("id", everyItem(notNullValue())) //Her objenin bir id alanına sahip olduğunu doğrular.
                    .body("title", everyItem(notNullValue())) //Her objenin bir title alanına sahip olduğunu doğrular.
                    .body("body", everyItem(notNullValue())) //Her objenin bir body alanına sahip olduğunu doğrular.
                    .extract();
    }

    // **Belirli Bir Değerin Doğrulanması:** Örneğin, `id` değeri `1` olan gönderinin `title` değerini doğrulayın.
    @Test
    public void validateValue(){

        Response response = given()
                .when()
                    .get("https://jsonplaceholder.typicode.com/posts/1");
        response
                .then()
                .statusCode(200);
        response.prettyPrint(); //response'u konsola yazdırdık.

        String title = response.jsonPath().getJsonObject("title");
        Assertions.assertEquals("sunt aut facere repellat provident occaecati excepturi optio reprehenderit", title);
    }

    // **Liste Uzunluğu Kontrolü:** JSON dizisinin en az 10 öğe içerdiğini doğrulayın.
    @Test
    public void validateListLength(){

        given()
                .when()
                    .get("https://jsonplaceholder.typicode.com/posts")
                .then()
                    .body("size()", greaterThanOrEqualTo(10))
                .log().all();
    }

    // **Dinamik Veri Kontrolleri:** `userId` alanlarının pozitif tamsayılar olduğunu doğrulayın.
    @Test
    public void validateDynamicData(){

        Response response =
                given()
                        .when()
                            .get("https://jsonplaceholder.typicode.com/posts")
                        .then()
                            .statusCode(200)
                            .extract()
                            .response();

        List<Integer> userIds = response.jsonPath().getList("userId");

        userIds.forEach(userId -> {
            Assertions.assertTrue(userId > 0, "UserId pozitif tamsayı değil!" + userId);
        });
    }
}
package Campus;

import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class PositionTest {

    Cookies cookies;


    @BeforeClass
    public void loginCampus() {

        baseURI="https://demo.mersys.io/";


        Map<String, String> credential = new HashMap<>();
        credential.put("username", "richfield.edu");
        credential.put("password", "Richfield2020!");

        cookies=


                given()
                        .contentType(ContentType.JSON)
                        .body(credential)

                        .when()

                        .post("auth/login")

                        .then()
                        .log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies()
        ;
    }

    String positionID;
    String positionName;
    String positionShortName;

    @Test
    public void addPosition() {

        positionName=randomName();
        positionShortName=randomCode();

        Position position=new Position();
        position.setName(positionName);
        position.setShortName(positionShortName);
        position.setTenantId("5fe0786230cc4d59295712cf");

        positionID=

                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(position)

                        .when()
                        .post("school-service/api/employee-position")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().jsonPath().getString("id")
        ;
    }



    @Test (dependsOnMethods = "addPosition")
    public void addPositionNegative() {

        positionShortName=randomCode();

        Position position=new Position();
        position.setName(positionName);
        position.setShortName(positionShortName);
        position.setTenantId("5fe0786230cc4d59295712cf");


                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(position)

                        .when()
                        .post("school-service/api/employee-position")

                        .then()
                        .log().body()
                        .statusCode(400)
                        .body("message", equalTo("The Position with Name \""+positionName+"\" already exists."))
        ;
    }

    @Test (dependsOnMethods = "addPositionNegative")
    public void editPosition() {

        positionName=randomName();
        positionShortName=randomCode();

        Position position=new Position();
        position.setName(positionName);
        position.setShortName(positionShortName);
        position.setId(positionID);
        position.setTenantId("5fe0786230cc4d59295712cf");

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(position)

                .when()
                .put("school-service/api/employee-position")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(positionName))
        ;
    }




    @Test (dependsOnMethods = "editPosition")
    public void deletePosition() {


        given()
                .cookies(cookies)
                .pathParam("positionID", positionID)

                .when()
                .delete("school-service/api/employee-position/{positionID}")

                .then()
                .statusCode(204)
        ;
    }

    @Test (dependsOnMethods = "deletePosition")
    public void deletePositionNegative() {


        given()
                .cookies(cookies)
                .pathParam("positionID", positionID)

                .when()
                .delete("school-service/api/employee-position/{positionID}")

                .then()
                .log().body()
                .statusCode(204)
        ;
    }

    @Test (dependsOnMethods = "deletePositionNegative")
    public void editPositionNegative() {

        positionName=randomName();
        positionShortName=randomCode();

        Position position=new Position();
        position.setName(positionName);
        position.setShortName(positionShortName);
        position.setId(positionID);
        position.setTenantId("5fe0786230cc4d59295712cf");

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(position)

                .when()
                .put("school-service/api/employee-position")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("Can't find Position"))
        ;
    }

    public String randomName() {

        return RandomStringUtils.randomAlphabetic(8);
    }

    public String randomCode() {

        return RandomStringUtils.randomAlphanumeric(4).toLowerCase();
    }

}
    class Position {

        private String id;
        private String name;
        private String shortName;
        private String translateName;
        private String tenantId;
        private String active;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public String getTenantId() {
            return tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "Position{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", shortName='" + shortName + '\'' +
                    ", translateName='" + translateName + '\'' +
                    ", tenantId='" + tenantId + '\'' +
                    ", active='" + active + '\'' +
                    '}';
        }
    }


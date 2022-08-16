package app.todo.test.todos.config;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Configuration
public class RestAssuredSpecificationConfig {

    @Value("${todo.base.url}")
    private String BASE_URL;

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    RequestSpecification requestPetStoreSpecification() {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .log().all()
                .when()
                .contentType(ContentType.JSON);
    }
}

package app.todo.test.todos;

import app.todo.test.todos.config.TestConfig;
import app.todo.test.todos.dto.TodoDto;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@TestPropertySource(locations = "/application.properties")
public class TodoAppCrudTest {

    @Autowired
    private RequestSpecification requestSpecification;

    @Value("${todo.log}")
    private String log;

    @Value("${todo.pass}")
    private String pass;

    @Test
    public void postAndGetTodoTest() {

        TodoDto todoDto = new TodoDto(new Random().nextInt(100), "String", true);

        // step 1 - post todo
        requestSpecification
                .body(todoDto)
                .post()
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_CREATED);

        // step 2 - get todos and filter
        TodoDto todoDtoResponse = Arrays.stream(requestSpecification
                .get()
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(TodoDto[].class))
                .filter(todo -> todo.getId().equals(todoDto.getId()))
                .findAny()
                .orElseThrow();

        //assert
        Assertions.assertEquals(todoDto, todoDtoResponse);
    }

    @Test
    public void putTodoTest() {

        // step 1 - post todo
        TodoDto todoDto = new TodoDto(new Random().nextInt(100), "String", true);

        requestSpecification
                .body(todoDto)
                .post()
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_CREATED);

        // step -2 update todo
        TodoDto todoUpdateDto = new TodoDto(todoDto.getId(), "StringNew", false);

        requestSpecification
                .body(todoUpdateDto)
                .put(String.valueOf(todoDto.getId()))
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK);

        // step 3 - get todos and filter
        TodoDto todoDtoResponse = Arrays.stream(requestSpecification
                .get()
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(TodoDto[].class))
                .filter(todo -> todo.getId().equals(todoDto.getId()))
                .findAny()
                .orElseThrow();

        //assert
        Assertions.assertEquals(todoUpdateDto, todoDtoResponse);
    }

    @Test
    public void deleteTodoTest() {

        // step 1 - post todo
        TodoDto todoDto = new TodoDto(new Random().nextInt(100), "String", true);

        requestSpecification
                .body(todoDto)
                .post()
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_CREATED);

        // step 2 - delete todo by id
        requestSpecification
                .auth()
                .preemptive()
                .basic(log, pass)
                .delete(String.valueOf(todoDto.getId()))
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        // step 3 - get todos and filter
        Optional<TodoDto> todoDtoResponse = Arrays.stream(requestSpecification
                .get()
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(TodoDto[].class))
                .filter(todo -> todo.getId().equals(todoDto.getId()))
                .findAny();

        //assert
        Assertions.assertTrue(todoDtoResponse.isEmpty());
    }

}

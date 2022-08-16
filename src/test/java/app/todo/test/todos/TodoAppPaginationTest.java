package app.todo.test.todos;

import app.todo.test.todos.config.TestConfig;
import app.todo.test.todos.dto.TodoDto;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "/application.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TodoAppPaginationTest {

    private static final Integer LIMIT = 10;

    @Autowired
    private RequestSpecification requestSpecification;

    @BeforeAll
    void addData() {
        for (int i = 0; i < LIMIT; i++) {
            TodoDto todoDto = new TodoDto(new Random().nextInt(1000), "String", true);

            // step 1 - post todo
            requestSpecification
                    .body(todoDto)
                    .post()
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.SC_CREATED);
        }
    }

    @ParameterizedTest
    @MethodSource("generateData")
    public void paginationTest(List<Integer> list) {

        List<TodoDto> todoDtoResponse = Arrays.asList(requestSpecification
                .param("offset", list.get(0))
                .param("limit", list.get(1))
                .get()
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(TodoDto[].class));

        Assertions.assertEquals(todoDtoResponse.size(), list.get(1));

    }

    static Stream<Arguments> generateData() {
        return Stream.of(
                Arguments.of(List.of(0, 5)),
                Arguments.of(List.of(0, 10)),
                Arguments.of(List.of(1, 5)),
                Arguments.of(List.of(2, 3))
        );
    }
}

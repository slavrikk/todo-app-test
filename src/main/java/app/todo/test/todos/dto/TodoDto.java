package app.todo.test.todos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodoDto {

    private Integer id;
    private String text;
    private Boolean completed;
}

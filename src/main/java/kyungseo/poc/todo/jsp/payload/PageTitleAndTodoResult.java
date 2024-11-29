package kyungseo.poc.todo.jsp.payload;

import kyungseo.poc.todo.jsp.service.TodoServiceData;
import lombok.Getter;

import java.util.List;

@Getter
public class PageTitleAndTodoResult {
    String title;
    List<TodoDto> todos;

    public PageTitleAndTodoResult(String title, List<TodoDto> todos) {
        this.title = title;
        this.todos = todos;
    }

    public static PageTitleAndTodoResult of(String category, TodoServiceData todoData) {
        String pageTitle;
        List<TodoDto> todos;

        if (category.equals("today")) {
            pageTitle = "Today's todo list";
            todos = todoData.getAllTodayTodos();
        } else if (category.equals("done")) {
            pageTitle = "Completed todo list";
            todos = todoData.getCompletedTodayTodos();
        } else if (category.equals("pending")) {
            pageTitle = "Ongoing todo list";
            todos = todoData.getPendingTodos();
        } else { // category == "all"
            pageTitle = "All todo list";
            todos = todoData.getAllTodos();
        }

        return new PageTitleAndTodoResult(pageTitle, todos);
    }
}

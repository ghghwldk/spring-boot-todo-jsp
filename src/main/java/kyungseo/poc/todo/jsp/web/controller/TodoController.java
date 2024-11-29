/* ============================================================================
 * KYUNGSEO.PoC > Development Templates for building Web Apps
 *
 * Copyright 2023 Kyungseo Park <Kyungseo.Park@gmail.com>
 * ----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================================= */

package kyungseo.poc.todo.jsp.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kyungseo.poc.todo.jsp.payload.TodoDto;
import kyungseo.poc.todo.jsp.service.TodoService;
import kyungseo.poc.todo.jsp.service.TodoServiceData;
import kyungseo.poc.todo.jsp.web.validation.TodoValidator;

import java.util.List;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/todos")
public class TodoController {
    private final TodoService todoService;
    private final TodoValidator todoValidator;

    @GetMapping({"", "/"}) // List retrieval
    public String getList(final HttpServletRequest request, final ModelMap model) {
        model.addAttribute("todos", todoService.getTodosByUser("kyungseo"));
        return "todo/list";
    }

    @GetMapping({"/new"}) // Create single todo
    public String showAddTodoForm(Model model) {
        model.addAttribute("todo", new TodoDto());
        model.addAttribute("mode", FormModeEnum.REGISTER.getValue());
// mode for registering
        return "todo/form";
    }

    @GetMapping("/{id}") // View single todo
    public String showUpdateTodoForm(@PathVariable("id") Long id, final ModelMap model) {
        TodoDto todo = todoService.getTodoById(id);
        model.addAttribute("todo", todo);
        model.addAttribute("mode", FormModeEnum.UPDATE.getValue());
// mode for updating
        return "todo/form";
    }

    @PostMapping("/save")
    public String updateTodo(@Valid @ModelAttribute("todo") final TodoDto todo, final BindingResult result, final ModelMap model) {
        todoValidator.validate(todo, result);
        if (result.hasErrors()) {
            return "todo/form";
        }
        todoService.saveTodo(todo);
        return "redirect:/todos";
    }

    @GetMapping("/{id}/done") // Change status
    public String updateIsDone(@PathVariable("id") final Long id, final ModelMap modell, RedirectAttributes redirectAttributes) {
        TodoDto todo = todoService.getTodoById(id);
        todo.setDone(!todo.isDone()); // Toggle isDone
        todoService.saveTodo(todo);
        redirectAttributes.addFlashAttribute("message", "The status of todo (" + id + ") has been updated.");
        return "redirect:/todos";
    }

    //@DeleteMapping("/{id}")
    @GetMapping("/{id}/delete") // Delete single todo
    public String deleteTodo(@PathVariable("id") final Long id, final ModelMap model, RedirectAttributes redirectAttributes) {
        todoService.deleteTodo(id);
        redirectAttributes.addFlashAttribute("message", "Todo (" + id + ") has been deleted.");
        return "redirect:/todos";
    }



    @GetMapping({"/dashboard"})
    public String dashboardAll(
            @RequestParam(value = "category", defaultValue = "all") final String category,
            final ModelMap model) {

        TodoServiceData todoData = new TodoServiceData(todoService.getTodosByUser("kyungseo"));
        PageTitleAndTodoResult result = getTitleAndTodos(category, todoData);

        model.addAttribute("todos", result.todos);
        model.addAttribute("title", result.title);
        return "todo/dashboard";
    }

    private PageTitleAndTodoResult getTitleAndTodos(String category, TodoServiceData todoData) {
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

    private static class PageTitleAndTodoResult {
        String title;
        List<TodoDto> todos;

        PageTitleAndTodoResult(String title, List<TodoDto> todos) {
            this.title = title;
            this.todos = todos;
        }
    }
}

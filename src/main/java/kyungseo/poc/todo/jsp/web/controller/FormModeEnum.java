package kyungseo.poc.todo.jsp.web.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FormModeEnum {
    REGISTER("등록"),
    UPDATE("갱신");

    private final String value;
}

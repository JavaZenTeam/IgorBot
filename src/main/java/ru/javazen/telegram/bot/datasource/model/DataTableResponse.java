package ru.javazen.telegram.bot.datasource.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class DataTableResponse<T> {
    private Integer draw;
    private Integer recordsTotal;
    private Integer recordsFiltered;
    private List<T> data;
}

package ru.javazen.telegram.bot.datasource.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@ToString
public class DataTableRequest {
    private Map<String, Object> search;
    private int draw;
    private int start;
    private int length;
    private List<Map<String, Object>> order;

    public String getSearchValue() {
        return Optional.ofNullable(search)
                .map(map -> map.get("value"))
                .map(String.class::cast)
                .orElse(null);
    }

    public Integer getOrderColumn() {
        return Optional.ofNullable(order)
                .map(List::iterator)
                .filter(Iterator::hasNext)
                .map(Iterator::next)
                .map(map -> map.get("column"))
                .map(String.class::cast)
                .map(Integer::parseInt)
                .orElse(null);
    }

    public String getOrderDir() {
        return Optional.ofNullable(order)
                .map(List::iterator)
                .filter(Iterator::hasNext)
                .map(Iterator::next)
                .map(map -> map.get("dir"))
                .map(String.class::cast)
                .orElse(null);
    }
}

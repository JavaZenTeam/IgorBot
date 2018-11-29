package ru.javazen.telegram.bot.help;

import lombok.Data;

import java.util.List;

@Data
public class Help {

    private String welcome;
    private List<Feature>  features;

    @Data
    public static class Feature {

        private String title;
        private String description;
        private String details;
    }
}
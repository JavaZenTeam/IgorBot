package ru.javazen.telegram.bot.analysis.tone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public interface TextToneAnalyzer {

    AnalyzedResponse analyze(String text);

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class AnalyzedResponse {
        private String text;
        private String tone;
        private BigDecimal score;
    }
}

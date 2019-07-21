package ru.javazen.telegram.bot.analysis.tone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

public class RemoteTextToneAnalyzer implements TextToneAnalyzer {

    private static final String ANALYZE_TEXT_TONE_API = "/api/v1/tone?text={text}";
    private RestTemplate restTemplate = new RestTemplate();

    @Value("${text-analysis.url}")
    private String textAnalyzerService;

    @Override
    public AnalyzedResponse analyze(String text) {

        return restTemplate.getForObject(textAnalyzerService + ANALYZE_TEXT_TONE_API,
                AnalyzedResponse.class, text);
    }
}

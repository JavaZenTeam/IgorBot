package ru.javazen.telegram.bot.handler;

import com.amazonaws.services.polly.model.VoiceId;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.telegram.telegrambots.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResultVoice;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.handler.base.InlineQueryHandler;
import ru.javazen.telegram.bot.service.VoiceService;

import java.util.Calendar;
import java.util.HashMap;

@Slf4j
public class SayTextHandler implements InlineQueryHandler {
    private static final int QUERY_LENGTH_THRESHOLD = 3;
    private static final int VOICE_GENERATION_PAUSE = 2;
    private static final int RANDOM_LENGTH = 10;

    private final VoiceService voiceService;
    private TaskScheduler taskScheduler = new DefaultManagedTaskScheduler();
    private HashMap<Integer, String> queries = new HashMap<>();

    public SayTextHandler(VoiceService voiceService) {
        this.voiceService = voiceService;
    }

    @Override
    public boolean handle(InlineQuery inlineQuery, AbsSender sender) throws TelegramApiException {
        if (inlineQuery.getQuery().length() > QUERY_LENGTH_THRESHOLD) {
            queries.put(inlineQuery.getFrom().getId(), inlineQuery.getQuery());
            scheduleTask(inlineQuery, sender);
            return true;
        }
        return false;
    }

    private void scheduleTask(InlineQuery inlineQuery, AbsSender sender) {
        Calendar date = Calendar.getInstance();
        date.add(Calendar.SECOND, VOICE_GENERATION_PAUSE);

        String text = inlineQuery.getQuery();
        Integer userId = inlineQuery.getFrom().getId();

        taskScheduler.schedule(() -> {
            if (text.equals(queries.get(userId))) {
                String fileUrl = voiceService.getAsFileLink(text, VoiceId.Maxim);
                String randomId = RandomStringUtils.randomAlphanumeric(RANDOM_LENGTH);

                InlineQueryResultVoice audio = new InlineQueryResultVoice();
                audio.setTitle(text).setVoiceUrl(fileUrl).setId(randomId);

                AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery();
                answerInlineQuery.setResults(audio).setInlineQueryId(inlineQuery.getId());
                try {
                    sender.execute(answerInlineQuery);
                } catch (TelegramApiException e) {
                    throw new RuntimeException("Can't execute answerInlineQuery", e);
                }
            }
        }, date.getTime());
    }
}

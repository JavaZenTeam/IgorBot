package ru.javazen.telegram.bot.handler;

import com.amazonaws.services.polly.model.VoiceId;
import org.telegram.telegrambots.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.api.methods.send.SendAudio;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResultAudio;
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResultVoice;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.javazen.telegram.bot.service.VoiceService;
import ru.javazen.telegram.bot.util.MessageHelper;

public class InlineQuery implements UpdateHandler {
    private VoiceService voiceService;

    public InlineQuery(VoiceService voiceService){
        this.voiceService=voiceService;
    }
    @Override
    public boolean handle(Update update, AbsSender sender) throws TelegramApiException {
        if(update.hasInlineQuery())
        {
            String fileUrl=voiceService.getAsFileLink(update.getInlineQuery().getQuery(), VoiceId.Maxim);
            InlineQueryResultVoice audio=new InlineQueryResultVoice();
            audio.setTitle("Maxim").setVoiceUrl(fileUrl).setId("1237234");
            AnswerInlineQuery answerInlineQuery=new AnswerInlineQuery();
            answerInlineQuery.setResults(audio).setInlineQueryId(update.getInlineQuery().getId()+"");
            sender.execute(answerInlineQuery);
            return true;
        }
        return false;
    }
}

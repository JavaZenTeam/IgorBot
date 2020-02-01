package ru.javazen.telegram.bot.service;

import com.amazonaws.services.polly.model.VoiceId;

public interface VoiceService {
    String getAsFileLink(String text, Integer userId, VoiceId voiceId);

    byte[] synthesize(String text, VoiceId voiceId);



}

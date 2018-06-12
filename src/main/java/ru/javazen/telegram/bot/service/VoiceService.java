package ru.javazen.telegram.bot.service;

import com.amazonaws.services.polly.model.VoiceId;

public interface VoiceService {
    public String getAsFileLink(String text, VoiceId voiceId);
    public byte[] synthesize(String text, VoiceId voiceId);
}

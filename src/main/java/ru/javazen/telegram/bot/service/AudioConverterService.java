package ru.javazen.telegram.bot.service;

import ws.schild.jave.EncoderException;

import java.io.IOException;

public interface AudioConverterService {
    
    byte[] convertVorbisToOpus(byte[] vorbisData) throws IOException, EncoderException;
}
package ru.javazen.telegram.bot.service.impl;

import lombok.extern.slf4j.Slf4j;
import ru.javazen.telegram.bot.service.AudioConverterService;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
public class AudioConverterServiceImpl implements AudioConverterService {

    @Override
    public byte[] convertVorbisToOpus(byte[] vorbisData) throws IOException, EncoderException {
        Path tempInputFile = null;
        Path tempOutputFile = null;

        try {
            tempInputFile = Files.createTempFile("input_" + UUID.randomUUID(), ".ogg");
            tempOutputFile = Files.createTempFile("output_" + UUID.randomUUID(), ".opus");

            Files.write(tempInputFile, vorbisData);

            AudioAttributes audioAttributes = new AudioAttributes();
            audioAttributes.setCodec("libopus");
            audioAttributes.setBitRate(64000);
            audioAttributes.setChannels(1);
            audioAttributes.setSamplingRate(48000);

            EncodingAttributes encodingAttributes = new EncodingAttributes();
            encodingAttributes.setInputFormat("ogg");
            encodingAttributes.setOutputFormat("opus");
            encodingAttributes.setAudioAttributes(audioAttributes);

            Encoder encoder = new Encoder();
            encoder.encode(new MultimediaObject(tempInputFile.toFile()), tempOutputFile.toFile(), encodingAttributes);

            return Files.readAllBytes(tempOutputFile);

        } finally {
            if (tempInputFile != null) {
                try {
                    Files.deleteIfExists(tempInputFile);
                } catch (IOException e) {
                    log.warn("Failed to delete temp input file: {}", tempInputFile, e);
                }
            }
            if (tempOutputFile != null) {
                try {
                    Files.deleteIfExists(tempOutputFile);
                } catch (IOException e) {
                    log.warn("Failed to delete temp output file: {}", tempOutputFile, e);
                }
            }
        }
    }
}
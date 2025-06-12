package ru.javazen.telegram.bot.service.impl;

import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import com.amazonaws.services.polly.model.VoiceId;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import ru.javazen.telegram.bot.client.FileServiceClient;
import ru.javazen.telegram.bot.service.AudioConverterService;
import ru.javazen.telegram.bot.service.VoiceService;

@Slf4j
public class VoiceServiceImpl implements VoiceService {

    private AmazonPolly amazonClient;
    private FileServiceClient fileServiceClient;
    private AudioConverterService audioConverterService;

    public VoiceServiceImpl(FileServiceClient fileServiceClient, AmazonPolly amazonPolly, AudioConverterService audioConverterService) {
        this.fileServiceClient = fileServiceClient;
        this.amazonClient = amazonPolly;
        this.audioConverterService = audioConverterService;
    }

    @Override
    public String getAsFileLink(String text, VoiceId voiceId) {
        //TODO caching
        return fileServiceClient.uploadFile(synthesizeAsOpus(text, voiceId), text);
    }

    @SneakyThrows
    @Override
    public byte[] synthesize(String text, VoiceId voiceId) {
        SynthesizeSpeechRequest synthesizeSpeechRequest = new SynthesizeSpeechRequest()
                .withOutputFormat(OutputFormat.Ogg_vorbis)
                .withVoiceId(voiceId)
                .withText(text);

        SynthesizeSpeechResult synthesizeSpeechResult = amazonClient.synthesizeSpeech(synthesizeSpeechRequest);
        return IOUtils.toByteArray(synthesizeSpeechResult.getAudioStream());
    }

    @SneakyThrows
    public byte[] synthesizeAsOpus(String text, VoiceId voiceId) {
        byte[] vorbisData = synthesize(text, voiceId);
        return audioConverterService.convertVorbisToOpus(vorbisData);
    }
}

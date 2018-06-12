package ru.javazen.telegram.bot.service.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.AmazonPollyClientBuilder;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import com.amazonaws.services.polly.model.VoiceId;
import ru.javazen.telegram.bot.service.VoiceService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class VoiceServiceImpl implements VoiceService{

    private AWSCredentials credentials;
    private AmazonPolly client;

    public VoiceServiceImpl(String accessKey, String secretKey){
        credentials=new BasicAWSCredentials(accessKey,secretKey);
        client=AmazonPollyClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion("eu-west-2").build();
    }

    @Override
    public String getAsFileLink(String text, VoiceId voiceId) {
        //здесь кароч ищем, нет ли у нас уже такого файла
        //если нет, синтезируем
        synthesize(text, voiceId);
        // загружаем на сервак
        // возвпращаем линку
        return "http://javazen.ru:8080/word-associations-0.0.1-SNAPSHOT/associations/find/гавно";
    }

    @Override
    public byte[] synthesize(String text, VoiceId voiceId) {
        SynthesizeSpeechRequest synthesizeSpeechRequest = new SynthesizeSpeechRequest()
                .withOutputFormat(OutputFormat.Ogg_vorbis)
                .withVoiceId(voiceId)
                .withText(text);

        SynthesizeSpeechResult synthesizeSpeechResult = client.synthesizeSpeech(synthesizeSpeechRequest);
        /*
        String outputFileName = "E:\\speech1.ogg";//TODO  РОТ ИБИВ
        try (FileOutputStream outputStream = new FileOutputStream(new File(outputFileName))) {
            byte[] buffer = new byte[2 * 1024];
            int readBytes;

            try (InputStream in = synthesizeSpeechResult.getAudioStream()){
                while ((readBytes = in.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, readBytes);
                }
            }
        } catch (Exception e) {
            System.err.println("Exception caught: " + e);
        }
        */
        return new byte[8];
    }
}

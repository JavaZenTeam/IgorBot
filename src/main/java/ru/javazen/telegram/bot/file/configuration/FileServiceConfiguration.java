package ru.javazen.telegram.bot.file.configuration;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import ru.javazen.telegram.bot.file.provider.FileRepository;
import ru.javazen.telegram.bot.file.provider.impl.FileSystemRepository;

@Configuration
public class FileServiceConfiguration implements EnvironmentAware {

    private String filesDirectory;

    @Bean
    public FileRepository fileProvider() {
        return new FileSystemRepository(filesDirectory);
    }

    @Override
    public void setEnvironment(Environment environment) {
        String filesDirectory = environment.getProperty("service.file.dir");
        if (filesDirectory == null) {
            filesDirectory = System.getProperty("user.home");
        }
        this.filesDirectory = filesDirectory;
    }
}

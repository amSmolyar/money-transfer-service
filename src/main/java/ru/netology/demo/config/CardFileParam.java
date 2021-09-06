package ru.netology.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "carddatabase")
public class CardFileParam {
    private static String filepath;
    private static String filename;

    public String getFilepath() {
        return (filepath == null) ? "." : filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFilename() {
        return (filename == null) ? "TestCardDataBase.txt" : filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}

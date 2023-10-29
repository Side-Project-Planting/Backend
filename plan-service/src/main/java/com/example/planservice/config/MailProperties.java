package com.example.planservice.config;

import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties("mail")
@Getter
@Setter
public class MailProperties {

    private String username;
    private String password;
    private String host;
    private String port;
    private String auth;
    private Starttls starttls;
    private Ssl ssl;

    public Properties toProperties() {
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", host);
        props.setProperty("mail.smtp.port", port);
        props.setProperty("mail.smtp.auth", auth);
        props.setProperty("mail.smtp.starttls.enable", starttls.getEnable());
        props.setProperty("mail.smtp.ssl.trust", ssl.getTrust());
        props.setProperty("mail.smtp.ssl.enable", ssl.getEnable());
        return props;
    }

    @Getter
    @Setter
    private static class Starttls {
        private String enable;
    }

    @Getter
    @Setter
    private static class Ssl {
        private String trust;
        private String enable;
    }


}

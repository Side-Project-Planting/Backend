package com.example.planservice.config;

import java.util.Properties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
        props.setProperty("mail.smtp.starttls.enable", String.valueOf(starttls.isEnable()));
        props.setProperty("mail.smtp.ssl.trust", ssl.getTrust());
        props.setProperty("mail.smtp.ssl.enable", String.valueOf(ssl.isEnable()));
        return props;
    }

    @Getter
    @Setter
    public static class Starttls {
        private boolean enable;
    }

    @Getter
    @Setter
    public static class Ssl {
        private String trust;
        private boolean enable;
    }
}

package com.example.planservice.application;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;

import jakarta.annotation.PostConstruct;

@Component
public class EmailService {
    private static final String INVITING_ANNOUNCEMENT = " 플랜에 링크를 눌러 초대를 수락할 수 있습니다.";
    private static final String INVITING_SUBJECT = "Planting의 플랜에 초대되었습니다";

    private Session session;

    @Value("${mail.username}")
    private String userEmail;

    @Value("${mail.password}")
    private String password;

    @Value("${mail.smtp.host}")
    private String host;

    @Value("${mail.smtp.port}")
    private String port;

    @Value("${mail.smtp.auth}")
    private String auth;

    @Value("${mail.smtp.starttls.enable}")
    private String starttlsEnable;

    @Value("${mail.smtp.ssl.trust}")
    private String sslTrust;

    @Value("${mail.smtp.ssl.enable}")
    private String sslEnable;

    @PostConstruct
    public void init() {
        session = initSession();
    }

    public void sendEmail(String to, String text) {
        try {
            final Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(userEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(INVITING_SUBJECT);
            String content = text + ' ' + INVITING_ANNOUNCEMENT;
            content += "<br>";
            content += "http://localhost/invite";

            message.setContent(content, "text/html; charset=utf-8");

            send(message);
        } catch (MessagingException e) {
            throw new ApiException(ErrorCode.SERVER_ERROR);
        }
    }

    protected void send(Message message) throws MessagingException {
        Transport.send(message);
    }

    private Session initSession() {
        final Properties props = new Properties();

        props.setProperty("mail.smtp.host", host);
        props.setProperty("mail.smtp.port", port);
        props.setProperty("mail.smtp.auth", auth);
        props.setProperty("mail.smtp.starttls.enable", starttlsEnable);
        props.setProperty("mail.smtp.ssl.trust", sslTrust);
        props.setProperty("mail.smtp.ssl.enable", sslEnable);

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userEmail, password);
            }
        });
    }

}

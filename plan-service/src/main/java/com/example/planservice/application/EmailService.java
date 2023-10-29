package com.example.planservice.application;

import com.example.planservice.config.MailProperties;
import com.example.planservice.exception.ApiException;
import com.example.planservice.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailService {
    private static final String INVITING_ANNOUNCEMENT = " 플랜에 링크를 눌러 초대를 수락할 수 있습니다.";
    private static final String INVITING_SUBJECT = "Planting의 플랜에 초대되었습니다";

    private Session session;

    @Autowired
    private MailProperties mailProperties;

    @PostConstruct
    public void init() {
        session = initSession();
    }

    public void sendEmail(String to, String text) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailProperties.getUsername()));
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
        return Session.getInstance(mailProperties.toProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailProperties.getUsername(), mailProperties.getPassword());
            }
        });
    }

}

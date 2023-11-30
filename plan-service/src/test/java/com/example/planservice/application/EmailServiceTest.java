package com.example.planservice.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.example.planservice.config.TestConfig;

@SpringBootTest
@Import(TestConfig.class)
class EmailServiceTest {

    @Autowired
    EmailService emailService;

    @Test
    void sendEmail() throws MessagingException {

        // given
        String recipient = "test@example.com";
        String text = "Hello, this is a test email.";
        EmailService emailServiceMock = Mockito.spy(emailService);
        Mockito.doNothing()
            .when(emailServiceMock)
            .send(any(Message.class));

        // when
        emailServiceMock.sendInviteEmail(recipient, text, "uuid");

        // then
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        Mockito.verify(emailServiceMock, times(1))
            .send(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertEquals(sentMessage.getRecipients(Message.RecipientType.TO)[0].toString(), recipient);
        assertEquals(sentMessage.getSubject(), "Planting의 플랜에 초대되었습니다");
    }
}

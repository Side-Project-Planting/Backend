package com.example.planservice.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmailServiceTest {

    @Autowired
    EmailService emailService;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException, MessagingException {
        emailService.init();
    }

    @Test
    void sendEmail() throws MessagingException {
        // given
        String recipient = "test@example.com";
        String text = "Hello, this is a test email.";
        EmailService emailServiceMock = Mockito.spy(emailService);
        Mockito.doNothing().when(emailServiceMock).send(any(Message.class));

        // when
        emailServiceMock.sendEmail(recipient, text);

        // then
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        Mockito.verify(emailServiceMock, times(1)).send(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertEquals(sentMessage.getRecipients(Message.RecipientType.TO)[0].toString(), recipient);
        assertEquals(sentMessage.getSubject(), "Planting의 플랜에 초대되었습니다");
    }
}

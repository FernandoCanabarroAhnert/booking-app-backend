package com.fernandocanabarro.booking_app_backend.utils;

import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fernandocanabarro.booking_app_backend.services.EmailService;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Service
@Profile("it") 
public class MockEmailService implements EmailService {

    @Override
    public void sendEmail(Mail mail) {
        System.out.println("[FAKE EMAIL] Email não enviado em ambiente de teste.");
    }

    @Override
    public Mail createEmail(String emailTo, String subject, Map<String, Object> variables, String templateName) {
        return new Mail(new Email("from@test.com"), subject, new Email(emailTo), new Content("text/plain", "Fake content"));

    }


}

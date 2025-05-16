package com.fernandocanabarro.booking_app_backend.services.impl;

import java.io.IOException;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.fernandocanabarro.booking_app_backend.services.EmailService;
import com.fernandocanabarro.booking_app_backend.services.exceptions.EmailException;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile("!it")
public class EmailServiceImpl implements EmailService {

    private final SendGrid sendGrid;
    private final SpringTemplateEngine templateEngine;

    private final String EMAIL_FROM = "ahnertfernando499@gmail.com";

    @Override
    public void sendEmail(Mail mail) {
        try {
            Request request = new Request();
            request.setEndpoint("mail/send");
            request.setMethod(Method.POST);
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            if (response.getStatusCode() >= 400) {
                throw new EmailException("Error sending email: " + response.getBody());
            }
        }
        catch (IOException e) {
            throw new EmailException(e.getMessage());
        }
    }

    @Override
    public Mail createEmail(String emailTo, String subject, Map<String, Object> variables, String templateName) {
        Context context = new Context();
        context.setVariables(variables);
        String content = templateEngine.process(templateName, context);
        Content emailContent = new Content("text/html", content);

        Email to = new Email(emailTo);
        Email from = new Email(EMAIL_FROM, "Ybanté Hotéis");
        return new Mail(from, subject, to, emailContent);
    }



}

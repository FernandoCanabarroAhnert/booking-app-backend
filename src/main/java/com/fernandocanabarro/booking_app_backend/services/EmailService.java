package com.fernandocanabarro.booking_app_backend.services;

import java.util.Map;

import com.sendgrid.helpers.mail.Mail;

public interface EmailService {

    void sendEmail(Mail mail);
    Mail createEmail(String emailTo, String subject, Map<String, Object> variables, String templateName);

}

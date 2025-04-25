package com.fernandocanabarro.booking_app_backend.services;

import com.sendgrid.helpers.mail.Mail;

public interface EmailService {

    void sendEmail(Mail mail);
    Mail createEmail(String fullName, String emailTo, String code);

}

package com.template.webserver.emailSender;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class EmailSender {
    private Properties properties;
    private Email email;

    public EmailSender(Email email) {
        this.email = email;
        properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

    }

    public void sendmail() throws AddressException, MessagingException, IOException {
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email.getMailFrom(), email.getMailFromPassword());
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(email.getMailFrom(), false));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getMailTo()));
        msg.setSubject(email.getMailSubject());
        msg.setContent(email.getMailContent(), "text/html");
        msg.setSentDate(new Date());
        Transport.send(msg);
    }
}

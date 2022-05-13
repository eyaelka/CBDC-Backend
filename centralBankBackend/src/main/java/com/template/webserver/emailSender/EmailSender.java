package com.template.webserver.emailSender;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class EmailSender {
    private Properties props;
    private EmailFromTo emailFromTo;

    public EmailSender(EmailFromTo emailFromTo){
        this.emailFromTo = emailFromTo;
        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
    }

    public void sendmail() throws AddressException, MessagingException, IOException {
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailFromTo.getEmailFrom(), emailFromTo.getEmailFromPassWord());
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(emailFromTo.getEmailFrom(), false));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailFromTo.getEmailReceiver()));
        msg.setSubject(emailFromTo.getEmailSubject());
        msg.setContent(emailFromTo.getEmailContent(), "text/html");
        msg.setSentDate(new Date());
        Transport.send(msg);
    }
}
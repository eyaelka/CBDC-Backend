package com.template.webserver.emailSender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Email {
    private String mailFrom;
    private String mailFromPassword;
    private String mailTo;
    private String mailSubject;
    private String mailContent;
}

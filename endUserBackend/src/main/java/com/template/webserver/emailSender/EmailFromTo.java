package com.template.webserver.emailSender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailFromTo {
    private String emailFrom;
    private String emailFromPassWord;
    private String emailReceiver;
    private String emailSubject;
    private String emailContent;
}

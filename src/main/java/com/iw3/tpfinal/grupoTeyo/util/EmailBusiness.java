package com.iw3.tpfinal.grupoTeyo.util;

import com.iw3.tpfinal.grupoTeyo.model.business.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailBusiness {

    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text) throws BusinessException {
        log.trace("Enviando mail subject={} a: {}", subject, to);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@lpn.com.ar");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
        }
    }
}

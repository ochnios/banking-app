package pl.ochnios.bankingbe.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final Logger log = LoggerFactory.getLogger(EmailService.class);

    public void sendEmail(String subject, String message) {
        log.info(subject + "; " + message);
    }
}

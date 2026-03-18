package com.alejandro.cv.contact;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ContactMailService {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
    private static final Logger log = LoggerFactory.getLogger(ContactMailService.class);

    private final JavaMailSender mailSender;
    private final String recipientEmail;
    private final String senderEmail;
    private final String smtpHost;

    public ContactMailService(
            JavaMailSender mailSender,
            @Value("${app.contact.mail.to:alejo.valenciarivera@gmail.com}") String recipientEmail,
            @Value("${app.contact.mail.from:}") String senderEmail,
            @Value("${spring.mail.host:}") String smtpHost) {
        this.mailSender = mailSender;
        this.recipientEmail = recipientEmail;
        this.senderEmail = senderEmail;
        this.smtpHost = smtpHost;
    }

    public void send(ContactMessageRequest request) throws MessagingException {
        if (!StringUtils.hasText(smtpHost)) {
            log.error("Mail delivery is not configured: spring.mail.host is empty.");
            throw new IllegalStateException(
                    "Mail delivery is not configured. Set MAIL_HOST together with MAIL_USERNAME and MAIL_PASSWORD.");
        }

        if (!StringUtils.hasText(senderEmail)) {
            log.error("Mail delivery is not configured: sender email is empty.");
            throw new IllegalStateException(
                    "Mail delivery is not configured. Set MAIL_USERNAME, MAIL_PASSWORD, and APP_CONTACT_MAIL_FROM.");
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(mimeMessage, false, StandardCharsets.UTF_8.name());

        helper.setTo(recipientEmail);
        helper.setFrom(senderEmail);
        helper.setReplyTo(request.fromEmail().trim());
        helper.setSubject("[Portfolio Contact] " + request.subject().trim());
        helper.setText(buildMessageBody(request), false);

        try {
            mailSender.send(mimeMessage);
        } catch (MailException ex) {
            log.error(
                    "Failed to send contact email via SMTP host '{}' from '{}' to '{}'.",
                    smtpHost,
                    senderEmail,
                    recipientEmail,
                    ex);
            throw new IllegalStateException(
                    "The message could not be sent right now. Check the configured SMTP credentials and try again.",
                    ex);
        }
    }

    private String buildMessageBody(ContactMessageRequest request) {
        return """
                New message received from the CV contact form.

                Sender email:
                %s

                Subject:
                %s

                Message:
                %s

                Sent at:
                %s
                """.formatted(
                request.fromEmail().trim(),
                request.subject().trim(),
                request.message().trim(),
                ZonedDateTime.now().format(TIMESTAMP_FORMAT));
    }
}

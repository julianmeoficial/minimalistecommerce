package com.digital.mecommerces.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;

import jakarta.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration class for email-related beans.
 * Provides a mock JavaMailSender when email is disabled.
 */
@Configuration
@Slf4j
public class EmailConfig {

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    /**
     * Creates a JavaMailSender bean.
     * When email is disabled (local development), it returns a mock implementation
     * that doesn't attempt to connect to any SMTP server.
     *
     * @return JavaMailSender instance
     */
    @Bean
    public JavaMailSender javaMailSender() {
        if (!emailEnabled) {
            // When email is disabled, use a no-op implementation
            // This prevents any connection attempts to SMTP servers
            log.info("Email is disabled. Using mock JavaMailSender.");
            return new NoOpMailSender();
        } else {
            // In a real environment, these would be configured from properties
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("smtp.example.com");
            mailSender.setPort(587);
            mailSender.setUsername("username");
            mailSender.setPassword("password");

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.debug", "false");

            return mailSender;
        }
    }

    /**
     * A no-operation implementation of JavaMailSender that logs actions
     * instead of actually sending emails.
     */
    private static class NoOpMailSender implements JavaMailSender {
        @Override
        public MimeMessage createMimeMessage() {
            return new JavaMailSenderImpl().createMimeMessage();
        }

        @Override
        public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
            return new JavaMailSenderImpl().createMimeMessage(contentStream);
        }

        @Override
        public void send(MimeMessage mimeMessage) throws MailException {
            log.debug("Mock mail sender: Would send MimeMessage");
        }

        @Override
        public void send(MimeMessage... mimeMessages) throws MailException {
            log.debug("Mock mail sender: Would send {} MimeMessages", mimeMessages.length);
        }

        @Override
        public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
            log.debug("Mock mail sender: Would send MimeMessagePreparator");
        }

        @Override
        public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {
            log.debug("Mock mail sender: Would send {} MimeMessagePreparators", mimeMessagePreparators.length);
        }

        @Override
        public void send(SimpleMailMessage simpleMessage) throws MailException {
            log.debug("Mock mail sender: Would send SimpleMailMessage to: {}", simpleMessage.getTo());
        }

        @Override
        public void send(SimpleMailMessage... simpleMessages) throws MailException {
            log.debug("Mock mail sender: Would send {} SimpleMailMessages", simpleMessages.length);
        }
    }
}

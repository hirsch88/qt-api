package ch.w3tec.qt.api.domain.service.mail;

import ch.w3tec.qt.api.domain.exception.EmailSendingException;
import ch.w3tec.qt.api.persistence.entity.Tournament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final MailContentBuilder mailContentBuilder;

    @Value("${spring.mail.username}")
    private String sender;

    @Value("${app.gui.path}")
    private String appPath;

    @Autowired
    public MailService(
            JavaMailSender mailSender,
            MailContentBuilder mailContentBuilder
    ) {
        this.mailSender = mailSender;
        this.mailContentBuilder = mailContentBuilder;
    }

    public void sendConfirmationOfTheCreatedTournament(Tournament tournament) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(sender);
            messageHelper.setTo(tournament.getEmail());
            messageHelper.setSubject("QuickTournament: " + tournament.getName());
            String content = mailContentBuilder.build(tournament, appPath);
            messageHelper.setText(content, true);
        };
        try {
            mailSender.send(messagePreparator);
        } catch (MailException exception) {
            LOGGER.error("FAILED Sending mail => ", exception);
            throw new EmailSendingException("Could not send email");
        }

    }

}

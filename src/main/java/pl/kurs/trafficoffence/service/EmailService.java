package pl.kurs.trafficoffence.service;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.trafficoffence.config.Passes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


@Service
@Transactional
public class EmailService implements IEmailService {

    private final JavaMailSender emailSender;
    private final Configuration configuration;

    public EmailService(JavaMailSender emailSender, Configuration configuration) {
        this.emailSender = emailSender;
        this.configuration = configuration;
    }

    @Override
    @Async
    public void sendMessageWithHTMLContent(String address, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);
        mimeMessageHelper.setFrom(Passes.emailUserName);
        mimeMessageHelper.setTo(address);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(htmlContent, true);
        emailSender.send(message);
    }

    @Override
    @Async
    public void sendMessage(String address, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(Passes.emailUserName);
        message.setTo(address);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }


    @Override
    public String getEmailContentForInformPersonAboutBan(String text) throws IOException, TemplateException {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("date", text);
        configuration.getTemplate("ban.ftlh").process(model, stringWriter);
        return stringWriter.getBuffer().toString();
    }
}

package pl.kurs.trafficoffence.event.listener;

import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import pl.kurs.trafficoffence.event.OnInformPersonEvent;
import pl.kurs.trafficoffence.service.IEmailService;

import javax.mail.MessagingException;
import java.io.IOException;

@Component
public class MakeInformPersonListener implements ApplicationListener<OnInformPersonEvent> {

    private static final String emailSubject = "Driving license ban";
    private final Logger logger = LoggerFactory.getLogger(MakeInformPersonListener.class);

    private IEmailService emailService;

    public MakeInformPersonListener(IEmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(OnInformPersonEvent event) {

        String personEmail = event.getPerson().getEmail();

        String emailMessage;
        try {
            emailMessage = emailService.getEmailContentForInformPersonAboutBan(event.getDateOfBan().toString());
            emailService.sendMessageWithHTMLContent(personEmail, emailSubject, emailMessage);
        } catch (TemplateException | IOException | MessagingException e) {
            e.printStackTrace();
            emailMessage = "You lost your driving license\n Your driving license has been blocked. You have exceeded the 24 penalty points of the day: " + event.getDateOfBan().toString();
            emailService.sendMessage(personEmail, emailSubject, emailMessage);
            logger.error("Error occurred for sending confirmation email with template", e);
        }


    }
}

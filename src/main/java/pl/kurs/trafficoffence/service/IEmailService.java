package pl.kurs.trafficoffence.service;

import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.IOException;

public interface IEmailService {

    void sendMessageWithHTMLContent(String address, String subject, String text) throws MessagingException;

    void sendMessage(String address, String subject, String text);

    String getEmailContentForInformPersonAboutBan(String text) throws IOException, TemplateException;

}

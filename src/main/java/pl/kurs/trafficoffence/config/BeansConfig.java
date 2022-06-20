package pl.kurs.trafficoffence.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.modelmapper.ModelMapper;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import pl.kurs.trafficoffence.model.Offence;
import pl.kurs.trafficoffence.model.Person;
import pl.kurs.trafficoffence.repository.OffenceRepository;
import pl.kurs.trafficoffence.repository.PersonRepository;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;

@Configuration
public class BeansConfig {

    @Bean
    public ModelMapper createModelMapper() {
        return new ModelMapper();
    }

    private static final String dateFormat = "yyyy-MM-dd";
    private static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.simpleDateFormat(dateTimeFormat);
            builder.deserializers(new LocalDateDeserializer(DateTimeFormatter.ofPattern(dateFormat)));
            builder.deserializers(new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
        };
    }

    @Bean
    public ApplicationRunner dataInitializer(OffenceRepository offenceRepository, PersonRepository personRepository) {
        return args -> {
            Person person1 = new Person("Jan", "Kowalski", "lukz1184@gmail.com", "17252379565", new HashSet<>(), LocalDate.of(2022, 6, 20));
            Person person2 = new Person("Anna", "Kowalska", "lukz1184@gmail.com", "93102298064", new HashSet<>(), null);
            Person person3 = new Person("Maria", "Kowalska", "lukz1184@gmail.com", "10301257867", new HashSet<>(), null);
            Person person4 = new Person("Franciszek", "Kowalski", "lukz1184@gmail.com", "22261007161", new HashSet<>(), null);
            personRepository.saveAll(Arrays.asList(person1, person2, person3, person4));
            Offence offence1 = new Offence(LocalDateTime.of(2022, 6, 20, 10, 0), 10, new BigDecimal("100.00"), "Przekroczenie prędkości", person1);
            Offence offence11 = new Offence(LocalDateTime.of(2022, 6, 20, 11, 0), 10, new BigDecimal("100.00"), "Przekroczenie prędkości", person1);
            Offence offence12 = new Offence(LocalDateTime.of(2022, 6, 20, 12, 0), 5, new BigDecimal("100.00"), "Przekroczenie prędkości", person1);
            Offence offence2 = new Offence(LocalDateTime.of(2022, 6, 20, 10, 0), 10, new BigDecimal("200.00"), "Przekroczenie prędkości", person2);
            Offence offence3 = new Offence(LocalDateTime.of(2022, 6, 20, 15, 0), 10, new BigDecimal("300.00"), "Przekroczenie prędkości", person2);
            offenceRepository.saveAll(Arrays.asList(offence1, offence11, offence12, offence2, offence3));


        };
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(Passes.emailUserName);
        mailSender.setPassword(Passes.emailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }


    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster;
        eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return eventMulticaster;
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(s -> !s.endsWith("/error"))
                .build();
    }
}

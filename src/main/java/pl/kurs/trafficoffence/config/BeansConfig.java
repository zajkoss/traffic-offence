package pl.kurs.trafficoffence.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.modelmapper.ModelMapper;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.kurs.trafficoffence.model.Offence;
import pl.kurs.trafficoffence.model.Person;
import pl.kurs.trafficoffence.repository.OffenceRepository;
import pl.kurs.trafficoffence.repository.PersonRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;

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
//    @Profile("dev")
    public ApplicationRunner dataInitializer(OffenceRepository offenceRepository, PersonRepository personRepository) {
        return args -> {
            Person person1 = new Person("Jan","Kowalski","lukz1184@gmail.com","17252379565",new HashSet<>());
            Person person2 = new Person("Anna","Kowalska","lukz1184@gmail.com","93102298064",new HashSet<>());
            Person person3 = new Person("Maria","Kowalska","lukz1184@gmail.com","10301257867",new HashSet<>());
            Person person4 = new Person("Franciszek","Kowalski","lukz1184@gmail.com","22261007161",new HashSet<>());
            personRepository.saveAll(Arrays.asList(person1,person2,person3,person4));
            Offence offence1 = new Offence(LocalDateTime.of(2022, 6, 20, 10, 0),10, new BigDecimal("100.00"),"Przekroczenie prędkości",person1);
            Offence offence2 = new Offence(LocalDateTime.of(2022, 6, 20, 10, 0),10, new BigDecimal("200.00"),"Przekroczenie prędkości",person2);
            Offence offence3 = new Offence(LocalDateTime.of(2022, 6, 20, 15, 0),10, new BigDecimal("300.00"),"Przekroczenie prędkości",person2);
            offenceRepository.saveAll(Arrays.asList(offence1,offence2,offence3));



        };
    }
}

package pl.kurs.trafficoffence.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.kurs.trafficoffence.TrafficOffenceApplication;
import pl.kurs.trafficoffence.model.Offence;
import pl.kurs.trafficoffence.model.Person;
import pl.kurs.trafficoffence.repository.OffenceRepository;
import pl.kurs.trafficoffence.repository.PersonRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;

@SpringBootTest(classes = TrafficOffenceApplication.class)
class PersonServiceTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private OffenceRepository offenceRepository;

    @Autowired
    private PersonService personService;


    private Person person1;
    private Person person2;
    private Offence offence1;
    private Offence offence2;
    private Offence offence3;

    @BeforeEach
    void setUp(){
        personRepository.deleteAll();
        offenceRepository.deleteAll();
        person1 = new Person("Jan","Kowalski","lukz1184@gmail.com","17252379565",new HashSet<>(), null);
        person2 = new Person("Anna", "Kowalska", "lukz1184@gmail.com", "93102298064", new HashSet<>(), null);
        personRepository.save(person1);
        personRepository.save(person2);
        offence1 = new Offence(LocalDateTime.of(2022,6,20,10,0),5,new BigDecimal("3000.0"),new HashSet<>(),person1);
        offence2 = new Offence(LocalDateTime.of(2022,7,20,10,0),5,new BigDecimal("5000.0"),new HashSet<>(),person1);
        offence3 = new Offence(LocalDateTime.of(1999,6,20,10,0),5,new BigDecimal("4000.0"),new HashSet<>(),person1);
        offenceRepository.save(offence1);
        offenceRepository.save(offence2);
        offenceRepository.save(offence3);
    }

    @Test
    public void givenLast_whenGettingListOfUsers_thenCorrect() {
        String lastNameQuery = "lastnme:Kowals,name:Jan";
        personService.searchPerson(lastNameQuery);

    }



}
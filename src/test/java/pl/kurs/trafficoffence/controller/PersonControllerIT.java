package pl.kurs.trafficoffence.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.kurs.trafficoffence.TrafficOffenceApplication;
import pl.kurs.trafficoffence.command.CreatePersonCommand;
import pl.kurs.trafficoffence.dto.PersonDto;
import pl.kurs.trafficoffence.dto.PersonDtoWithOffencesSummary;
import pl.kurs.trafficoffence.model.Offence;
import pl.kurs.trafficoffence.model.Person;
import pl.kurs.trafficoffence.repository.OffenceRepository;
import pl.kurs.trafficoffence.repository.PersonRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TrafficOffenceApplication.class, properties = {"spring.h2.console.enabled=true"}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class PersonControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private OffenceRepository offenceRepository;

    private Person person1;
    private Person person2;
    private Offence offence1;
    private Offence offence2;
    private Offence offence3;


    @BeforeEach
    void setUp() {
        offenceRepository.deleteAll();
        personRepository.deleteAll();
        person1 = new Person("Jan", "Kowalski", "lukz1184@gmaail.com", "17052379565", new HashSet<>(), null);
        person2 = new Person("Anna", "Kowalska", "lukz1184@gmaill.com", "93102298064", new HashSet<>(), null);
        personRepository.save(person1);
        personRepository.save(person2);
        offence1 = new Offence(LocalDateTime.of(2022, 6, 20, 10, 0), 5, new BigDecimal("3000.0"), new HashSet<>(), person1);
        offence2 = new Offence(LocalDateTime.of(2022, 7, 20, 10, 0), 5, new BigDecimal("5000.0"), new HashSet<>(), person1);
        offence3 = new Offence(LocalDateTime.of(1999, 6, 20, 10, 0), 5, new BigDecimal("4000.0"), new HashSet<>(), person1);
        offenceRepository.save(offence1);
        offenceRepository.save(offence2);
        offenceRepository.save(offence3);
    }


    @Test
    public void shouldReturnSummaryFor2Persons() throws Exception {
        String responseJson = mockMvc.perform(get("/person/search")
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        //then
        List<PersonDtoWithOffencesSummary> personsResponse = objectMapper.readValue(responseJson, new TypeReference<List<PersonDtoWithOffencesSummary>>() {
        });
        assertEquals(2, personsResponse.size());

    }


    @Test
    public void shouldReturnSummaryForOnePersonByName() throws Exception {
        String responseJson = mockMvc.perform(get("/person/search")
                        .param("name", "Jan")
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        //then
        List<PersonDtoWithOffencesSummary> personsResponse = objectMapper.readValue(responseJson, new TypeReference<List<PersonDtoWithOffencesSummary>>() {
        });
        assertEquals(1, personsResponse.size());
        PersonDtoWithOffencesSummary personResponse = personsResponse.get(0);
        assertTrue(personResponse.getPesel().equals("17052379565"));
        assertTrue(personResponse.getPoints() == 10);
        assertTrue(personResponse.getTotalOffences() == 3);
        assertTrue(personResponse.getLastname().equals("Kowalski"));
        assertTrue(personResponse.getName().equals("Jan"));
    }

    @Test
    public void shouldReturnSummaryFor2PersonsByLastName() throws Exception {
        String responseJson = mockMvc.perform(get("/person/search")
                        .param("lastname", "Kowal")
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        //then
        List<PersonDtoWithOffencesSummary> personsResponse = objectMapper.readValue(responseJson, new TypeReference<List<PersonDtoWithOffencesSummary>>() {
        });
        assertEquals(2, personsResponse.size());

    }

    @Test
    public void shouldAddNewPerson() throws Exception {
        //given
        Person person = new Person("Jan", "Mickiewicz", "lukz1181@gmail.com", "43070291006", new HashSet<>(), null);
        String createPersonCommandJson = objectMapper.writeValueAsString(modelMapper.map(person, CreatePersonCommand.class));

        //when
        String postRespondJson = mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPersonCommandJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PersonDto personDtoResponse = objectMapper.readValue(postRespondJson, PersonDto.class);
        long generatedId = personDtoResponse.getId();

        personRepository.findById(generatedId);
        PersonDto personDtoSaved = modelMapper.map(personRepository.findById(generatedId).get(), PersonDto.class);
        assertEquals(personDtoResponse, personDtoSaved);
    }

    @Test
    public void shouldThrowExceptionWhenTryAddNewPersonWithNotUniqueEmail() throws Exception {
        //given
        Person person = new Person("Luke", "Nowakiewicz", "lukz1184@gmaail.com", "43070291006", new HashSet<>(), null);
        String createPersonCommandJson = objectMapper.writeValueAsString(modelMapper.map(person, CreatePersonCommand.class));

        //when
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPersonCommandJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessages").isArray())
                .andExpect(jsonPath("$.errorMessages", hasSize(1)))
                .andExpect(jsonPath("$.errorMessages", hasItem("Property: email; value: 'lukz1184@gmaail.com'; message: Not unique value")))
                .andExpect(jsonPath("$.exceptionTypeName").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"));

    }


    @Test
    public void shouldReturnSummaryForPersonByHigherThan10() throws Exception {
        String responseJson = mockMvc.perform(get("/person/search")
                        .param("pointsFrom", "10")
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        //then
        List<PersonDtoWithOffencesSummary> personsResponse = objectMapper.readValue(responseJson, new TypeReference<List<PersonDtoWithOffencesSummary>>() {
        });
        assertEquals(1, personsResponse.size());
        PersonDtoWithOffencesSummary personResponse = personsResponse.get(0);
        assertTrue(personResponse.getPesel().equals("17052379565"));
        assertTrue(personResponse.getPoints() == 10);
        assertTrue(personResponse.getTotalOffences() == 3);
        assertTrue(personResponse.getLastname().equals("Kowalski"));
        assertTrue(personResponse.getName().equals("Jan"));
    }

    @Test
    public void shouldReturnSummaryForPersonByLessPointsThan10() throws Exception {
        String responseJson = mockMvc.perform(get("/person/search")
                        .param("pointsTo", "10")
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        //then
        List<PersonDtoWithOffencesSummary> personsResponse = objectMapper.readValue(responseJson, new TypeReference<List<PersonDtoWithOffencesSummary>>() {
        });
        assertEquals(1, personsResponse.size());
        PersonDtoWithOffencesSummary personResponse = personsResponse.get(0);
        assertTrue(personResponse.getPesel().equals("93102298064"));
        assertTrue(personResponse.getPoints() == 0);
        assertTrue(personResponse.getTotalOffences() == 0);
        assertTrue(personResponse.getLastname().equals("Kowalska"));
        assertTrue(personResponse.getName().equals("Anna"));
    }


    @Test
    public void shouldReturnSummaryForPersonByPointsBetween5And10() throws Exception {
        String responseJson = mockMvc.perform(get("/person/search")
                        .param("pointsTo", "10")
                        .param("pointsFrom", "5")
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        //then
        List<PersonDtoWithOffencesSummary> personsResponse = objectMapper.readValue(responseJson, new TypeReference<List<PersonDtoWithOffencesSummary>>() {
        });
        assertEquals(0, personsResponse.size());
    }

    @Test
    public void shouldReturnSummaryForPersonByLastname() throws Exception {
        String responseJson = mockMvc.perform(get("/person/search")
                        .param("lastname", "Kowals")
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        //then
        List<PersonDtoWithOffencesSummary> personsResponse = objectMapper.readValue(responseJson, new TypeReference<List<PersonDtoWithOffencesSummary>>() {
        });
        assertEquals(2, personsResponse.size());
    }

    @Test
    public void shouldReturnSummaryForPersonBornBefore1920() throws Exception {
        String responseJson = mockMvc.perform(get("/person/search")
                        .param("birthdayTo", "1920-01-01")
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        //then
        List<PersonDtoWithOffencesSummary> personsResponse = objectMapper.readValue(responseJson, new TypeReference<List<PersonDtoWithOffencesSummary>>() {
        });
        assertEquals(1, personsResponse.size());
        PersonDtoWithOffencesSummary personResponse = personsResponse.get(0);
        assertTrue(personResponse.getPesel().equals("17052379565"));
        assertTrue(personResponse.getPoints() == 10);
        assertTrue(personResponse.getTotalOffences() == 3);
        assertTrue(personResponse.getLastname().equals("Kowalski"));
        assertTrue(personResponse.getName().equals("Jan"));
    }


    @Test
    public void shouldReturnSummaryForPersonByBornAfter1920() throws Exception {
        String responseJson = mockMvc.perform(get("/person/search")
                        .param("birthdayFrom", "1919-12-31")
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        //then
        List<PersonDtoWithOffencesSummary> personsResponse = objectMapper.readValue(responseJson, new TypeReference<List<PersonDtoWithOffencesSummary>>() {
        });
        assertEquals(1, personsResponse.size());
        PersonDtoWithOffencesSummary personResponse = personsResponse.get(0);
        assertTrue(personResponse.getPesel().equals("93102298064"));
        assertTrue(personResponse.getPoints() == 0);
        assertTrue(personResponse.getTotalOffences() == 0);
        assertTrue(personResponse.getLastname().equals("Kowalska"));
        assertTrue(personResponse.getName().equals("Anna"));
    }


    @Test
    public void shouldReturnSummaryForPersonByBornBetween1920And1990() throws Exception {
        String responseJson = mockMvc.perform(get("/person/search")
                        .param("birthdayTo", "1990-01-01")
                        .param("birthdayFrom", "1919-12-31")
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        //then
        List<PersonDtoWithOffencesSummary> personsResponse = objectMapper.readValue(responseJson, new TypeReference<List<PersonDtoWithOffencesSummary>>() {
        });
        assertEquals(0, personsResponse.size());
    }



}
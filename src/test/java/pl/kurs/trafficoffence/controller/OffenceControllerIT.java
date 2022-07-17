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
import pl.kurs.trafficoffence.command.CreateOffenceCommand;
import pl.kurs.trafficoffence.dto.FaultDto;
import pl.kurs.trafficoffence.dto.OffenceDto;
import pl.kurs.trafficoffence.model.Fault;
import pl.kurs.trafficoffence.model.Offence;
import pl.kurs.trafficoffence.model.Parameterization;
import pl.kurs.trafficoffence.model.Person;
import pl.kurs.trafficoffence.repository.FaultRepository;
import pl.kurs.trafficoffence.repository.OffenceRepository;
import pl.kurs.trafficoffence.repository.ParameterizationRepository;
import pl.kurs.trafficoffence.repository.PersonRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TrafficOffenceApplication.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class OffenceControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private OffenceRepository offenceRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private FaultRepository faultRepository;

    @Autowired
    private ParameterizationRepository parameterizationRepository;

    private Person person1;
    private Person person2;
    private Person person3;
    private Offence offence1;
    private Offence offence2;
    private Offence offence3;
    private Fault fault1;
    private Fault fault2;
    private Fault fault3;

    @BeforeEach
    void setUp() {
        offenceRepository.deleteAll();
        personRepository.deleteAll();
        parameterizationRepository.deleteAll();
        parameterizationRepository.save(new Parameterization("limitOfPenalty","8000.00"));
        parameterizationRepository.save(new Parameterization("limitOfPoints","20"));
        person1 = new Person("Jan", "Kowalski", "lukz1184@gmail.com", "17252379565", new HashSet<>(), LocalDate.of(2022, 6, 20));
        person2 = new Person("Anna", "Kowalska", "lukz1184@gmail.com", "93102298064", new HashSet<>(), null);
        person3 = new Person("Maria", "Kowalska", "lukz1184@gmail.com", "10301257867", new HashSet<>(), null);
        person1 = personRepository.save(person1);
        person2 = personRepository.save(person2);
        person3 = personRepository.save(person3);
        fault1 = new Fault("Wyprzedzanie na pasach", 15, new BigDecimal("5000.00"), new HashSet<>(), false);
        fault2 = new Fault("Przekroczenie prędkości o 50km/h", 10, new BigDecimal("4000.00"), new HashSet<>(), false);
        fault3 = new Fault("Przekroczenie prędkości o 40km/h", 8, new BigDecimal("3000.00"), new HashSet<>(), false);
        fault1 = faultRepository.save(fault1);
        fault2 = faultRepository.save(fault2);
        fault3 = faultRepository.save(fault3);
        offence1 = new Offence(LocalDateTime.of(2022, 6, 15, 10, 0), 10, new BigDecimal("1000.00"), new HashSet<>(), person3);
        offence2 = new Offence(LocalDateTime.of(2022, 6, 16, 10, 0), 5, new BigDecimal("1000.00"), new HashSet<>(), person3);
        offence3 = new Offence(LocalDateTime.of(2022, 6, 17, 10, 0), 5, new BigDecimal("1000.00"), new HashSet<>(), person3);
        offence1 = offenceRepository.save(offence1);
        offence2 = offenceRepository.save(offence2);
        offence3 = offenceRepository.save(offence3);
    }

    @Test
    public void shouldReturn3Offences() throws Exception {
        List<Offence> listOfOffence = List.of(offence1, offence2, offence3);
        String responseJson = mockMvc.perform(get("/offence"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<OffenceDto> offenceListResponse = objectMapper.readValue(responseJson, new TypeReference<List<OffenceDto>>() {
        });
        assertEquals(3, offenceListResponse.size());
        assertTrue(offenceListResponse.containsAll(listOfOffence.stream().map(o -> modelMapper.map(o, OffenceDto.class)).collect(Collectors.toList())));
    }

    @Test
    public void shouldReturn1OffenceFor2PageAnd1Offset() throws Exception {
        String responseJson = mockMvc.perform(get("/offence")
                        .param("size", "1")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<OffenceDto> offenceListResponse = objectMapper.readValue(responseJson, new TypeReference<List<OffenceDto>>() {
        });
        assertEquals(1, offenceListResponse.size());
        assertTrue(offenceListResponse.contains(modelMapper.map(offence2, OffenceDto.class)));

    }

    @Test
    public void shouldReturnOffenceForId1() throws Exception {
        String responseJson = mockMvc.perform(get("/offence/" + offence1.getId()).characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        OffenceDto offenceResponse = objectMapper.readValue(responseJson, OffenceDto.class);
        assertEquals(modelMapper.map(offence1, OffenceDto.class), offenceResponse);

    }

    @Test
    public void shouldResponseNotFoundCodeWhenTryGetNoExistOffence() throws Exception {
        mockMvc.perform(get("/offence/" + 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessages").isArray())
                .andExpect(jsonPath("$.errorMessages", hasSize(1)))
                .andExpect(jsonPath("$.errorMessages", hasItem("99")))
                .andExpect(jsonPath("$.exceptionTypeName").value("EntityNotFoundException"))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"));

    }


    @Test
    public void shouldAddNewOffence() throws Exception {
        //given
        Offence offence = new Offence(LocalDateTime.of(2022, 6, 17, 10, 0), 20, new BigDecimal("8000.00"), new HashSet<>(), person2);
        OffenceDto offenceDto = modelMapper.map(offence, OffenceDto.class);
        CreateOffenceCommand createOffenceCommand = modelMapper.map(offence, CreateOffenceCommand.class);
        createOffenceCommand.setFaults(List.of(fault1.getId(),fault2.getId()));
        offenceDto.setFaults(List.of(modelMapper.map(fault1, FaultDto.class),modelMapper.map(fault2, FaultDto.class)));
        String createOffenceCommandJson = objectMapper.writeValueAsString(createOffenceCommand);


        //when
        String postRespondJson = mockMvc.perform(post("/offence")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createOffenceCommandJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long offenceGeneratedId = objectMapper.readValue(postRespondJson, OffenceDto.class).getId();
        offenceDto.setId(offenceGeneratedId);

        //then
        String responseJson = mockMvc.perform(get("/offence/" + offenceGeneratedId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        OffenceDto offenceDtoResponse = objectMapper.readValue(responseJson, OffenceDto.class);
        List<FaultDto> faultsFromOffenceDto = offenceDto.getFaults();
        List<FaultDto> faultsFromResponse = offenceDtoResponse.getFaults();
        assertTrue(faultsFromResponse.containsAll(faultsFromResponse));
        assertTrue(faultsFromResponse.containsAll(faultsFromOffenceDto));
        offenceDtoResponse.setFaults(null);
        offenceDto.setFaults(null);
        assertEquals(offenceDto, offenceDtoResponse);
    }


    @Test
    public void shouldThrowPersonHaveBanDrivingLicenseExceptionWhenTryAddOffenceForPersonWithBan() throws Exception {
        //given
        Offence offence = new Offence(LocalDateTime.of(2022, 6, 18, 11, 0), 12, new BigDecimal("500.00"), new HashSet<>(), person3);
        CreateOffenceCommand createOffenceCommand = modelMapper.map(offence, CreateOffenceCommand.class);
        createOffenceCommand.setFaults(List.of(fault1.getId()));
        String createOffenceCommandJson = objectMapper.writeValueAsString(createOffenceCommand);

        mockMvc.perform(post("/offence")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createOffenceCommandJson));


        offence = new Offence(LocalDateTime.of(2022, 6, 18, 13, 0), 12, new BigDecimal("500.00"), new HashSet<>(), person3);

        CreateOffenceCommand createOffenceCommand2 = modelMapper.map(offence, CreateOffenceCommand.class);
        createOffenceCommand2.setFaults(List.of(fault1.getId()));
        String createOffenceCommandJson2 = objectMapper.writeValueAsString(createOffenceCommand2);

        //when
        mockMvc.perform(post("/offence")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createOffenceCommandJson2))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessages").isArray())
                .andExpect(jsonPath("$.errorMessages", hasSize(1)))
                .andExpect(jsonPath("$.errorMessages", hasItem("Driving license ban, pesel: 10301257867, from: 2022-06-18")))
                .andExpect(jsonPath("$.exceptionTypeName").value("PersonHaveBanDrivingLicenseException"))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"));
    }


    @Test
    public void shouldThrowBadRequestCodeWhenTryAddOffenceWithFutureDate() throws Exception {
        //given
        Offence offence = new Offence(LocalDateTime.of(2099, 6, 17, 10, 0), 7, new BigDecimal("500.00"), new HashSet<>(), person2);
        CreateOffenceCommand createOffenceCommand = modelMapper.map(offence, CreateOffenceCommand.class);
        createOffenceCommand.setFaults(List.of(fault1.getId()));
        String createOffenceCommandJson = objectMapper.writeValueAsString(createOffenceCommand);

        //when
        mockMvc.perform(post("/offence")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createOffenceCommandJson))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessages").isArray())
                .andExpect(jsonPath("$.errorMessages", hasSize(1)))
                .andExpect(jsonPath("$.errorMessages", hasItem("Property: time; value: '2099-06-17T10:00'; message: must be a date in the past or in the present")))
                .andExpect(jsonPath("$.exceptionTypeName").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"));
    }


    @Test
    public void shouldThrowBadRequestCodeWhenTryAddOffenceWithNoFaults() throws Exception {
        //given
        Offence offence = new Offence(LocalDateTime.of(2022, 6, 17, 10, 0), 6, new BigDecimal("10.00"), new HashSet<>(), person2);
        CreateOffenceCommand createOffenceCommand = modelMapper.map(offence, CreateOffenceCommand.class);
        String createOffenceCommandJson = objectMapper.writeValueAsString(createOffenceCommand);

        //when
        mockMvc.perform(post("/offence")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createOffenceCommandJson))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessages").isArray())
                .andExpect(jsonPath("$.errorMessages", hasSize(1)))
                .andExpect(jsonPath("$.errorMessages", hasItem("Property: faults; value: '[]'; message: List of faults cannot be empty")))
                .andExpect(jsonPath("$.exceptionTypeName").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"));
    }

    @Test
    public void shouldThrowBadRequestCodeWhenTryAddOffenceWithWrongPESELNumberAndNotExistPerson() throws Exception {
        //given
        Offence offence = new Offence(LocalDateTime.of(2022, 6, 17, 10, 0), 6, new BigDecimal("10.00"), new HashSet<>(), person2);
        CreateOffenceCommand createOffenceCommand = modelMapper.map(offence, CreateOffenceCommand.class);
        createOffenceCommand.setPersonPesel("03102298064");
        createOffenceCommand.setFaults(List.of(fault1.getId()));
        String createOffenceCommandJson = objectMapper.writeValueAsString(createOffenceCommand);

        //when
        mockMvc.perform(post("/offence")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createOffenceCommandJson))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessages").isArray())
                .andExpect(jsonPath("$.errorMessages", hasSize(2)))
                .andExpect(jsonPath("$.errorMessages", hasItem("Property: personPesel; value: '03102298064'; message: Person for given PESEL not exists in system")))
                .andExpect(jsonPath("$.errorMessages", hasItem("Property: personPesel; value: '03102298064'; message: invalid Polish National Identification Number (PESEL)")))
                .andExpect(jsonPath("$.exceptionTypeName").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"));
    }


}
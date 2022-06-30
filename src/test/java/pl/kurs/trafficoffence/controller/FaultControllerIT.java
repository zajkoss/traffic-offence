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
import pl.kurs.trafficoffence.command.CreateFaultCommand;
import pl.kurs.trafficoffence.command.UpdateFaultCommand;
import pl.kurs.trafficoffence.dto.FaultDto;
import pl.kurs.trafficoffence.model.Fault;
import pl.kurs.trafficoffence.repository.FaultRepository;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TrafficOffenceApplication.class)
@AutoConfigureMockMvc
class FaultControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FaultRepository faultRepository;

    private Fault fault1;
    private Fault fault2;
    private Fault fault3;
    private Fault fault4;
    private Fault fault5;
    private Fault fault6;

    @BeforeEach
    void setUp() {
        faultRepository.deleteAll();
        fault1 = new Fault("Wyprzedzanie na pasach", 15, new BigDecimal("5000.00"), new HashSet<>(), false);
        fault2 = new Fault("Przekroczenie prędkości o 50km/h", 10, new BigDecimal("4000.00"), new HashSet<>(), false);
        fault3 = new Fault("Przekroczenie prędkości o 40km/h", 8, new BigDecimal("3000.00"), new HashSet<>(), false);
        fault4 = new Fault("Przekroczenie prędkości o 30km/h", 7, new BigDecimal("2000.00"), new HashSet<>(), false);
        fault5 = new Fault("Przekroczenie prędkości o 20km/h", 6, new BigDecimal("1000.00"), new HashSet<>(), false);
        fault6 = new Fault("Przekroczenie prędkości o 10km/h", 5, new BigDecimal("1000.00"), new HashSet<>(), false);
        faultRepository.save(fault1);
        faultRepository.save(fault2);
        faultRepository.save(fault3);
        faultRepository.save(fault4);
        faultRepository.save(fault5);
        faultRepository.save(fault6);
    }

    @Test
    public void shouldReturn2Faults() throws Exception {
        List<Fault> listOfOffence = List.of(fault1, fault2, fault3, fault4, fault5, fault6);
        String responseJson = mockMvc.perform(get("/fault"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<FaultDto> faultListResponse = objectMapper.readValue(responseJson, new TypeReference<List<FaultDto>>() {
        });
        assertEquals(6, faultListResponse.size());
        assertTrue(faultListResponse.containsAll(listOfOffence.stream().map(o -> modelMapper.map(o, FaultDto.class)).collect(Collectors.toList())));
    }

    @Test
    public void shouldReturnFaultForId() throws Exception {
        String responseJson = mockMvc.perform(get("/fault/" + fault1.getId()).characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        FaultDto faultResponse = objectMapper.readValue(responseJson, FaultDto.class);
        assertEquals(modelMapper.map(fault1, FaultDto.class), faultResponse);
    }

    @Test
    public void shouldAddNewFault() throws Exception {
        //given
        Fault fault = new Fault("Złe parkowanei", 1, new BigDecimal("500.00"), new HashSet<>(), false);
        FaultDto faultDto = modelMapper.map(fault, FaultDto.class);
        String createFaultCommandJson = objectMapper.writeValueAsString(modelMapper.map(fault, CreateFaultCommand.class));

        //when
        String postRespondJson = mockMvc.perform(post("/fault")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFaultCommandJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long faultGeneratedId = objectMapper.readValue(postRespondJson, FaultDto.class).getId();
        faultDto.setId(faultGeneratedId);

        //then
        String responseJson = mockMvc.perform(get("/fault/" + faultGeneratedId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        FaultDto faultDtoResponse = objectMapper.readValue(responseJson, FaultDto.class);
        assertEquals(faultDto, faultDtoResponse);
    }

    @Test
    public void shouldUpdateFault() throws Exception {
        //given
        Fault fault = new Fault("Jazda bez włączonych świateł", 1, new BigDecimal("200.00"), new HashSet<>(), false);
        fault = faultRepository.save(fault);

        fault.setPoints(2);
        fault.setPenalty(new BigDecimal("300.00"));
        FaultDto faultDto = modelMapper.map(fault, FaultDto.class);
        String updateFaultCommandJson = objectMapper.writeValueAsString(modelMapper.map(fault, UpdateFaultCommand.class));

        //when
        String postRespondJson = mockMvc.perform(put("/fault")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateFaultCommandJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();


        //then
        String responseJson = mockMvc.perform(get("/fault/" + fault.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        FaultDto faultDtoResponse = objectMapper.readValue(responseJson, FaultDto.class);
        assertEquals(faultDto, faultDtoResponse);
    }

    @Test
    public void shouldSoftUpdate() throws Exception {
        //given
        Fault fault = new Fault("Jazda bez włączonych świateł", 1, new BigDecimal("200.00"), new HashSet<>(), false);
        fault = faultRepository.save(fault);

        FaultDto faultDto = modelMapper.map(fault, FaultDto.class);
        String updateFaultCommandJson = objectMapper.writeValueAsString(modelMapper.map(fault, UpdateFaultCommand.class));

        //when
        String postRespondJson = mockMvc.perform(put("/fault/delete/" + fault.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        //then
        mockMvc.perform(get("/fault/" + fault.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessages").isArray())
                .andExpect(jsonPath("$.errorMessages", hasSize(1)))
                .andExpect(jsonPath("$.errorMessages", hasItem(fault.getId().toString())))
                .andExpect(jsonPath("$.exceptionTypeName").value("EntityNotFoundException"))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"));
    }

    @Test
    public void shouldReturnAllAvailableFaults() throws Exception {
        String responseJson = mockMvc.perform(get("/fault/search")
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        //then
        List<FaultDto> visitsResponse = objectMapper.readValue(responseJson, new TypeReference<List<FaultDto>>() {
        });
        assertEquals(6, visitsResponse.size());
    }

    @Test
    public void shouldReturnAllAvailableFaultsBetween4And10Points() throws Exception {
        String responseJson = mockMvc.perform(get("/fault/search")
                        .param("minPoints", "7")
                        .param("maxPoints", "10")
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        //then
        List<FaultDto> faultResponse = objectMapper.readValue(responseJson, new TypeReference<List<FaultDto>>() {
        });
        assertEquals(3, faultResponse.size());
        faultResponse.forEach(fault ->
                assertTrue(fault.getPoints() >= 7 && fault.getPoints() <= 10)
        );
    }

    @Test
    public void shouldReturnAllAvailableFaultsBetween0And1000Penalty() throws Exception {
        String responseJson = mockMvc.perform(get("/fault/search")
                        .param("maxPenalty", "1000.00")
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        //then
        List<FaultDto> faultResponse = objectMapper.readValue(responseJson, new TypeReference<List<FaultDto>>() {
        });
        assertEquals(2, faultResponse.size());
        faultResponse.forEach(fault ->
                assertTrue(fault.getPenalty().compareTo(new BigDecimal("1000")) <= 0)
        );
    }

    @Test
    public void shouldReturnAllAvailableFaultsWithWordPrędkości() throws Exception {
        String responseJson = mockMvc.perform(get("/fault/search")
                        .param("name", "prędkości")
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        //then
        List<FaultDto> faultResponse = objectMapper.readValue(responseJson, new TypeReference<List<FaultDto>>() {
        });
        assertEquals(5, faultResponse.size());
        faultResponse.forEach(fault ->
                assertTrue(fault.getName().contains("prędkości"))
        );
    }

}
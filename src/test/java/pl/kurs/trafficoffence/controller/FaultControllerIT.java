package pl.kurs.trafficoffence.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.web.servlet.MockMvc;
import pl.kurs.trafficoffence.TrafficOffenceApplication;
import pl.kurs.trafficoffence.command.CreateFaultCommand;
import pl.kurs.trafficoffence.command.UpdateFaultCommand;
import pl.kurs.trafficoffence.dto.FaultDto;
import pl.kurs.trafficoffence.model.Fault;
import pl.kurs.trafficoffence.repository.FaultRepository;
import pl.kurs.trafficoffence.service.FaultService;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TrafficOffenceApplication.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class FaultControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FaultRepository faultRepository;

    @Autowired
    private FaultService faultService;

    private Fault fault1;
    private Fault fault2;
    private Fault fault3;
    private Fault fault4;
    private Fault fault5;
    private Fault fault6;

    @BeforeEach
    void setUp() {
        faultRepository.deleteAll();
        fault1 = new Fault("Wyprzedzanie na pasach", 15, new BigDecimal("5000.00"), false);
        fault2 = new Fault("Przekroczenie prędkości o 50km/h", 10, new BigDecimal("4000.00"), false);
        fault3 = new Fault("Przekroczenie prędkości o 40km/h", 8, new BigDecimal("3000.00"), false);
        fault4 = new Fault("Przekroczenie prędkości o 30km/h", 7, new BigDecimal("2000.00"), false);
        fault5 = new Fault("Przekroczenie prędkości o 20km/h", 6, new BigDecimal("1000.00"), false);
        fault6 = new Fault("Przekroczenie prędkości o 10km/h", 5, new BigDecimal("1000.00"), false);
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
        Fault fault = new Fault("Złe parkowanei", 1, new BigDecimal("500.00"), false);
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
        Fault fault = new Fault("Parkowanie", 1, new BigDecimal("200.00"), false);
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
        Fault fault = new Fault("Jazda bez włączonych świateł", 1, new BigDecimal("200.00"), false);
        fault = faultRepository.save(fault);

        FaultDto faultDto = modelMapper.map(fault, FaultDto.class);
        String updateFaultCommandJson = objectMapper.writeValueAsString(modelMapper.map(fault, UpdateFaultCommand.class));

        //when
        String postRespondJson = mockMvc.perform(delete("/fault/" + fault.getId()))
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


    @Test
    public void shouldTrowExceptionWhenTryAddFaultWithNotUniqueName() throws Exception {
        //given
        Fault fault = new Fault("Brak tablicy rejestracyjnej", 1, new BigDecimal("50.00"), false);
        FaultDto faultDto = modelMapper.map(fault, FaultDto.class);
        String createFaultCommandJson = objectMapper.writeValueAsString(modelMapper.map(fault, CreateFaultCommand.class));

        mockMvc.perform(post("/fault")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFaultCommandJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(post("/fault")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFaultCommandJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessages").isArray())
                .andExpect(jsonPath("$.errorMessages", hasSize(1)))
                .andExpect(jsonPath("$.errorMessages", hasItem("Property: name; value: 'Brak tablicy rejestracyjnej'; message: Not unique fault name")))
                .andExpect(jsonPath("$.exceptionTypeName").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"));
    }


    @Test
    public void shouldTrowExceptionWhenTryUpdateFaultWithNotUniqueName() throws Exception {
        //given
        Fault fault = new Fault("Brak tablicy rejestracyjnej", 1, new BigDecimal("50.00"), false);
        String createFaultCommandJson = objectMapper.writeValueAsString(modelMapper.map(fault, CreateFaultCommand.class));
        faultRepository.save(fault);


        fault2.setName("Brak tablicy rejestracyjnej");
        String updateFaultCommandJson = objectMapper.writeValueAsString(modelMapper.map(fault2, UpdateFaultCommand.class));
        mockMvc.perform(put("/fault")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateFaultCommandJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessages").isArray())
                .andExpect(jsonPath("$.errorMessages", hasSize(1)))
                .andExpect(jsonPath("$.errorMessages", hasItem("Property: updateFaultCommand'; message: Not unique fault name")))
                .andExpect(jsonPath("$.exceptionTypeName").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"));
    }

    @Test
    public void shouldThrowOptimisticLockingFailureWhenTryDeleteFaultInTheSameTime() throws Exception {
        Fault fault = faultRepository.save(new Fault("Brak zapiętych pasów", 5, new BigDecimal("500.00"), false));
        assertEquals(0, fault.getVersion());

        final ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(() -> {
            faultService.softDelete(fault.getId());
        });
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        fault.setPoints(12);
        Exception exception = assertThrows(ObjectOptimisticLockingFailureException.class, () -> faultRepository.save(fault));
        SoftAssertions sa = new SoftAssertions();
        sa.assertThat(exception).isExactlyInstanceOf(ObjectOptimisticLockingFailureException.class);
        sa.assertAll();

        mockMvc.perform(get("/fault/" + fault.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldThrowOptimisticLockingFailureWhenTryEditFaultInTheSameTime() throws Exception {
        Fault fault = faultRepository.save(new Fault("Brak zapiętych pasów", 5, new BigDecimal("500.00"), false));
        assertEquals(0, fault.getVersion());
        fault.setPoints(6);

        final ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(() -> {
            faultService.update(fault);
        });
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        fault.setPoints(7);
        Exception exception = assertThrows(ObjectOptimisticLockingFailureException.class, () -> faultRepository.save(fault));
        SoftAssertions sa = new SoftAssertions();
        sa.assertThat(exception).isExactlyInstanceOf(ObjectOptimisticLockingFailureException.class);
        sa.assertAll();

        String responseJson = mockMvc.perform(get("/fault/" + fault.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Fault faultResponse = objectMapper.readValue(responseJson,Fault.class);
        fault.setPoints(6);
        faultResponse.setVersion(0);
        assertEquals(fault,faultResponse);
    }


}
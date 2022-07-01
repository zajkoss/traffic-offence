package pl.kurs.trafficoffence.controller;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.kurs.trafficoffence.command.CreateFaultCommand;
import pl.kurs.trafficoffence.command.QueryFaultsCommand;
import pl.kurs.trafficoffence.command.UpdateFaultCommand;
import pl.kurs.trafficoffence.dto.FaultDto;
import pl.kurs.trafficoffence.dto.StatusDto;
import pl.kurs.trafficoffence.model.Fault;
import pl.kurs.trafficoffence.service.IFaultService;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "fault", produces = "application/json;charset=UTF-8")
@Validated
public class FaultController {
    private final IFaultService faultService;
    private final ModelMapper mapper;

    public FaultController(IFaultService faultService, ModelMapper mapper) {
        this.faultService = faultService;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<FaultDto> getNotDeletedById(@PathVariable("id") long id) {
        return ResponseEntity.ok(
                mapper.map(faultService.getNotDeletedById(id).orElseThrow(
                                () -> new EntityNotFoundException(Long.toString(id)))
                        , FaultDto.class)
        );
    }

    @GetMapping()
    public ResponseEntity<List<FaultDto>> getAllNotDeleted(@PageableDefault Pageable pageable) {
        Page<Fault> loadedFaultPage = faultService.getAllNotDeleted(pageable);
        return ResponseEntity.ok(
                loadedFaultPage
                        .getContent()
                        .stream()
                        .map(fault -> mapper.map(fault, FaultDto.class))
                        .collect(Collectors.toList())
        );
    }

    @PostMapping
    public ResponseEntity<FaultDto> addFault(@RequestBody @Valid CreateFaultCommand createFaultCommand) {
        Fault newFault = mapper.map(createFaultCommand, Fault.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map(faultService.add(newFault), FaultDto.class));
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<StatusDto> softDelete(@PathVariable("id") long id) {
        faultService.softDelete(id);
        return ResponseEntity.ok().body(new StatusDto(Long.toString(id)));
    }

    @PutMapping()
    public ResponseEntity<FaultDto> updateFault(@RequestBody UpdateFaultCommand updateFaultCommand) {
        Fault updateFault = mapper.map(updateFaultCommand, Fault.class);
        return ResponseEntity.ok(mapper.map(faultService.update(updateFault), FaultDto.class));
    }

    @GetMapping("/search")
    public ResponseEntity<List<FaultDto>> searchFaults(@ModelAttribute @Valid QueryFaultsCommand queryFaultsCommand) {
        List<Fault> loadedFaults = faultService.searchFaults(
                queryFaultsCommand.getName() == null ? "" : queryFaultsCommand.getName(),
                queryFaultsCommand.getMinPoints() == null ? 0 : queryFaultsCommand.getMinPoints(),
                queryFaultsCommand.getMaxPoints() == null ? 24 : queryFaultsCommand.getMaxPoints(),
                queryFaultsCommand.getMinPenalty() == null ? BigDecimal.ZERO : queryFaultsCommand.getMinPenalty(),
                queryFaultsCommand.getMaxPenalty() == null ? new BigDecimal("5000.00") : queryFaultsCommand.getMaxPenalty()
        );
        return ResponseEntity.ok(
                loadedFaults
                        .stream()
                        .map(fault -> mapper.map(fault, FaultDto.class))
                        .collect(Collectors.toList())
        );
    }
}

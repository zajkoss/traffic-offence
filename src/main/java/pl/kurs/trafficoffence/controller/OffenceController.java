package pl.kurs.trafficoffence.controller;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.kurs.trafficoffence.command.CreateOffenceCommand;
import pl.kurs.trafficoffence.dto.OffenceDto;
import pl.kurs.trafficoffence.model.Offence;
import pl.kurs.trafficoffence.service.IOffenceService;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/offence", produces = "application/json;charset=UTF-8")
@Validated
public class OffenceController {

    private final IOffenceService offenceService;
    private final ModelMapper mapper;

    public OffenceController(IOffenceService offenceService, ModelMapper mapper) {
        this.offenceService = offenceService;
        this.mapper = mapper;
    }

    @GetMapping()
    public ResponseEntity<List<OffenceDto>> getAll(
            @PageableDefault Pageable pageable
    ) {
        Page<Offence> loadedOffencePage = offenceService.getAll(pageable);
        return ResponseEntity.ok(
                loadedOffencePage
                        .getContent()
                        .stream()
                        .map(offence -> mapper.map(offence, OffenceDto.class))
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OffenceDto> getOffenceById(@PathVariable("id") long id) {
        return ResponseEntity.ok(
                mapper.map(offenceService.get(id).orElseThrow(
                        () -> new EntityNotFoundException(Long.toString(id))
                ), OffenceDto.class)
        );
    }

    @PostMapping
    public ResponseEntity<OffenceDto> addOffence(@RequestBody @Valid CreateOffenceCommand createOffenceCommand) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map(offenceService.add(createOffenceCommand), OffenceDto.class));
    }

}

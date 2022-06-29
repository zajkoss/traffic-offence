package pl.kurs.trafficoffence.controller;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.kurs.trafficoffence.command.CreatePersonCommand;
import pl.kurs.trafficoffence.command.QueryPersonCommand;
import pl.kurs.trafficoffence.dto.PersonDto;
import pl.kurs.trafficoffence.dto.PersonDtoWithOffencesSummary;
import pl.kurs.trafficoffence.model.Person;
import pl.kurs.trafficoffence.service.IPersonService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/person", produces = "application/json;charset=UTF-8")
@Validated
public class PersonController {
    private final IPersonService personService;
    private final ModelMapper mapper;

    public PersonController(IPersonService personService, ModelMapper mapper) {
        this.personService = personService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<PersonDto> addPerson(@RequestBody @Valid CreatePersonCommand createPersonCommand) {
        Person newPerson = mapper.map(createPersonCommand, Person.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map(personService.add(newPerson),PersonDto.class));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PersonDtoWithOffencesSummary>> searchPerson(@ModelAttribute @Valid QueryPersonCommand queryPersonCommand) {
        List<PersonDtoWithOffencesSummary> loadedPersonWithOffenceSummary = personService.searchPerson(
                queryPersonCommand.getName() == null ? "" : queryPersonCommand.getName(),
                queryPersonCommand.getLastname() == null ? "" : queryPersonCommand.getLastname(),
                queryPersonCommand.getPesel() == null ? "" : queryPersonCommand.getPesel()
        );
        return ResponseEntity.ok(loadedPersonWithOffenceSummary);
    }

}

package pl.kurs.trafficoffence.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.trafficoffence.dto.PersonDtoWithOffencesSummary;
import pl.kurs.trafficoffence.exception.EmptyPeselNumberException;
import pl.kurs.trafficoffence.exception.NoEmptyIdException;
import pl.kurs.trafficoffence.exception.NoEntityException;
import pl.kurs.trafficoffence.model.Person;
import pl.kurs.trafficoffence.repository.PersonRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PersonService implements IPersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public Optional<Person> getByPesel(String pesel) {
        return personRepository.findByPesel(Optional.ofNullable(pesel).orElseThrow(() -> new EmptyPeselNumberException(pesel)));
    }

    @Override
    public Person add(Person person) {
        if (person == null)
            throw new NoEntityException();
        if (person.getId() != null)
            throw new NoEmptyIdException(person.getId());
        return personRepository.save(person);
    }

    @Override
    public List<PersonDtoWithOffencesSummary> searchPerson(String name, String surname, String pesel) {
        List<Person> persons = personRepository.findAllByNameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndPesel(name, surname, pesel);
        return persons.stream().map(person -> summaryPersonOffences(person)).collect(Collectors.toList());
    }

    private PersonDtoWithOffencesSummary summaryPersonOffences(Person person){
        Optional<Integer> pointsFor2Years = person.getOffences().stream()
                .filter(offence -> offence.getTime().isAfter(LocalDateTime.now().minusYears(2)))
                .map(offence -> offence.getPoints())
                .reduce((offence, offence2) -> offence + offence2);
        PersonDtoWithOffencesSummary personWithOffences = new PersonDtoWithOffencesSummary();
        personWithOffences.setId(person.getId());
        personWithOffences.setName(person.getName());
        personWithOffences.setLastname(person.getLastname());
        personWithOffences.setPesel(person.getPesel());
        personWithOffences.setTotalOffences(person.getOffences().size());
        personWithOffences.setPoints(pointsFor2Years.orElse(0));
        return personWithOffences;
    }
}

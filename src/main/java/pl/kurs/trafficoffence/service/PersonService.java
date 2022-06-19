package pl.kurs.trafficoffence.service;

import org.springframework.stereotype.Service;
import pl.kurs.trafficoffence.exception.EmptyPeselNumberException;
import pl.kurs.trafficoffence.model.Person;
import pl.kurs.trafficoffence.repository.PersonRepository;

import java.util.Optional;

@Service
public class PersonService implements IPersonService{

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public Optional<Person> getByPesel(String pesel) {

        return personRepository.findByPesel(Optional.ofNullable(pesel).orElseThrow(() -> new EmptyPeselNumberException(pesel)));
    }
}

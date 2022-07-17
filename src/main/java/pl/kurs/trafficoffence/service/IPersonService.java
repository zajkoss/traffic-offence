package pl.kurs.trafficoffence.service;

import pl.kurs.trafficoffence.dto.PersonDtoWithOffencesSummary;
import pl.kurs.trafficoffence.model.Person;

import java.util.List;
import java.util.Optional;

public interface IPersonService {

    Optional<Person> getByPesel(String pesel);

    Person add(Person person);

    List<PersonDtoWithOffencesSummary> searchPerson(String name, String surname, String pesel);

    List<PersonDtoWithOffencesSummary> searchPerson(String criteria);


}

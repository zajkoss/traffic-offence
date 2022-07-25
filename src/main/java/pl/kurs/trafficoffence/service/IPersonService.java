package pl.kurs.trafficoffence.service;

import pl.kurs.trafficoffence.dto.PersonDtoWithOffencesSummary;
import pl.kurs.trafficoffence.model.Person;
import pl.kurs.trafficoffence.predicate.SearchPersonQuery;

import java.util.List;
import java.util.Optional;

public interface IPersonService {

    Optional<Person> getByPesel(String pesel);

    Person add(Person person);

    List<PersonDtoWithOffencesSummary> searchPerson(SearchPersonQuery searchPersonQuery);



}

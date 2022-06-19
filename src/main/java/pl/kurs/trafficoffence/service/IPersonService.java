package pl.kurs.trafficoffence.service;

import pl.kurs.trafficoffence.model.Person;

import java.util.Optional;

public interface IPersonService {

    Optional<Person> getByPesel(String pesel);

}

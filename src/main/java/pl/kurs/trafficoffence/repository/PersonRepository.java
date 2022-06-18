package pl.kurs.trafficoffence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.trafficoffence.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {

}

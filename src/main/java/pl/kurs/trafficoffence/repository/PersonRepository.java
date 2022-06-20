package pl.kurs.trafficoffence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.trafficoffence.model.Person;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    boolean existsByPesel(String peselNumber);

    Optional<Person> findByPesel(String peselNumber);

}

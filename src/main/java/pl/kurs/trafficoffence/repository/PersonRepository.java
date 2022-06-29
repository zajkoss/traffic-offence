package pl.kurs.trafficoffence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kurs.trafficoffence.model.Person;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    boolean existsByPesel(String peselNumber);

    Optional<Person> findByPesel(String peselNumber);

    @Query("SELECT p FROM Person p INNER JOIN Offence o WHERE o.person = p AND p.name LIKE '%?1%' AND p.lastname = '%?2%' AND p.pesel = '%?3%' ")
    List<Person> findAllByNameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndPesel(String name, String lastname, String pesel);

}

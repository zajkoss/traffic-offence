package pl.kurs.trafficoffence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.kurs.trafficoffence.model.Person;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    boolean existsByPesel(String peselNumber);

    Optional<Person> findByPesel(String peselNumber);

    @Query("SELECT DISTINCT p FROM Person p LEFT JOIN Offence o ON p.pesel = o.person.pesel WHERE p.name LIKE CONCAT('%',:name,'%') AND p.lastname LIKE CONCAT('%',:lastname,'%') AND p.pesel LIKE CONCAT('%',:pesel,'%') ")
    List<Person> findAllByNameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndPesel(@Param("name") String name, @Param("lastname") String lastname,@Param("pesel")  String pesel);

}

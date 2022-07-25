package pl.kurs.trafficoffence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import pl.kurs.trafficoffence.model.Person;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long>, QuerydslPredicateExecutor<Person> {

    boolean existsByPesel(String peselNumber);

    Optional<Person> findByPesel(String peselNumber);

    boolean existsByEmail(String email);

}

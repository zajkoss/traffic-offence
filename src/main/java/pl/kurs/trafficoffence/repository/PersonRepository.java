package pl.kurs.trafficoffence.repository;

import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.data.repository.query.Param;
import pl.kurs.trafficoffence.model.Person;
import pl.kurs.trafficoffence.model.QPerson;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long>, QuerydslPredicateExecutor<Person>, QuerydslBinderCustomizer<QPerson> {

    boolean existsByPesel(String peselNumber);

    Optional<Person> findByPesel(String peselNumber);

    boolean existsByEmail(String email);

    @Query("SELECT DISTINCT p FROM Person p LEFT JOIN Offence o ON p.pesel = o.person.pesel WHERE p.name LIKE CONCAT('%',:name,'%') AND p.lastname LIKE CONCAT('%',:lastname,'%') AND p.pesel LIKE CONCAT('%',:pesel,'%') ")
    List<Person> findAllByNameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndPesel(@Param("name") String name, @Param("lastname") String lastname, @Param("pesel") String pesel);

    @Override
    default  void customize(QuerydslBindings bindings, QPerson root){
        bindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
        bindings.excluding(root.email);
    }

}

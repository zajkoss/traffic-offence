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

public interface PersonRepository extends JpaRepository<Person, Long>, QuerydslPredicateExecutor<Person>{

    boolean existsByPesel(String peselNumber);

    Optional<Person> findByPesel(String peselNumber);

    boolean existsByEmail(String email);

}

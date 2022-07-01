package pl.kurs.trafficoffence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.trafficoffence.model.Parameterization;

import java.util.Optional;

public interface ParameterizationRepository extends JpaRepository<Parameterization, Long> {

    Optional<Parameterization> findByKeyParam(String key);
}

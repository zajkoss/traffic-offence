package pl.kurs.trafficoffence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.trafficoffence.model.Offence;

public interface OffenceRepository extends JpaRepository<Offence, Long> {

}

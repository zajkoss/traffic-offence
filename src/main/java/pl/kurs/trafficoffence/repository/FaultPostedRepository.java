package pl.kurs.trafficoffence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.trafficoffence.model.FaultPosted;

public interface FaultPostedRepository extends JpaRepository<FaultPosted, Long> {

}

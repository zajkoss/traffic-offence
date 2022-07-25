package pl.kurs.trafficoffence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.kurs.trafficoffence.model.Fault;
import pl.kurs.trafficoffence.model.FaultPosted;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface FaultPostedRepository extends JpaRepository<FaultPosted, Long> {

}

package pl.kurs.trafficoffence.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.kurs.trafficoffence.model.Fault;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IFaultService {

    Fault add(Fault fault);

    Optional<Fault> getNotDeletedById(Long id);

    Page<Fault> getAllNotDeleted(Pageable pageable);

    void softDelete(Long id);

    Fault update(Fault fault);

    List<Fault> searchFaults(String name, Integer minPoints, Integer maxPoints, BigDecimal minPenalty, BigDecimal maxPenalty);

}

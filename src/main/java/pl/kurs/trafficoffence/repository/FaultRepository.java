package pl.kurs.trafficoffence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.kurs.trafficoffence.model.Fault;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface FaultRepository extends JpaRepository<Fault, Long> {

    Page<Fault> findAllByDeleted(Pageable pageable, boolean deleted);

    Optional<Fault> findByIdAndDeleted(Long in, boolean deleted);

    boolean existsByNameAndAndDeleted(String name,boolean deleted);

    boolean existsByNameAndAndDeletedAndIdNot(String name,boolean deleted,Long id);

    List<Fault> findAllByPointsBetweenAndPenaltyBetweenAndNameContainingIgnoreCaseAndDeleted(
            Integer minPoints,
            Integer maxPoints,
            BigDecimal minPenalty,
            BigDecimal maxPenalty,
            String name,
            boolean deleted
    );

    @Query("SELECT f FROM Fault f WHERE f.id IN (:faults)")
    List<Fault> findAllByListOfId(@Param("faults") List<Long> faultsId);


}

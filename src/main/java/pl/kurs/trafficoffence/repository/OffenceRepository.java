package pl.kurs.trafficoffence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kurs.trafficoffence.model.Offence;
import pl.kurs.trafficoffence.model.Person;

import java.time.LocalDateTime;

public interface OffenceRepository extends JpaRepository<Offence, Long> {

    @Query("SELECT SUM(o.points) FROM Offence o where o.person = ?1 and o.time >= ?2")
    long sumPointsByPeselAndTime(Person person, LocalDateTime time);
}

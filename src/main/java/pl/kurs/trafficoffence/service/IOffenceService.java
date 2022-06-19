package pl.kurs.trafficoffence.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.kurs.trafficoffence.model.Offence;

import java.util.Optional;

public interface IOffenceService {

    Offence add(Offence offence);

    Optional<Offence> get(Long id);

    Page<Offence> getAll(Pageable pageable);
}

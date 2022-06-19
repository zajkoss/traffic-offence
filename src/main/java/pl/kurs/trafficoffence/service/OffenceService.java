package pl.kurs.trafficoffence.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.kurs.trafficoffence.exception.EmptyIdException;
import pl.kurs.trafficoffence.exception.NoEmptyIdException;
import pl.kurs.trafficoffence.exception.NoEntityException;
import pl.kurs.trafficoffence.model.Offence;
import pl.kurs.trafficoffence.repository.OffenceRepository;

import java.util.Optional;

@Service
public class OffenceService implements IOffenceService {

    private final OffenceRepository repository;

    public OffenceService(OffenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Offence add(Offence offence) {
        if (offence == null)
            throw new NoEntityException();
        if (offence.getId() != null)
            throw new NoEmptyIdException(offence.getId());
        if ((offence.getPoints() + repository.sumPointsByPeselAndTime(offence.getPerson(), offence.getTime())) > 24) {
            //todo send email, boolean for ban driving and addtional time
        }
        return repository.save(offence);
    }

    @Override
    public Optional<Offence> get(Long id) {
        return repository.findById(Optional.ofNullable(id).orElseThrow(() -> new EmptyIdException(id)));
    }

    @Override
    public Page<Offence> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
}

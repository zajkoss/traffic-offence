package pl.kurs.trafficoffence.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.trafficoffence.exception.EmptyIdException;
import pl.kurs.trafficoffence.exception.NoEmptyIdException;
import pl.kurs.trafficoffence.exception.NoEntityException;
import pl.kurs.trafficoffence.model.Fault;
import pl.kurs.trafficoffence.repository.FaultRepository;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FaultService implements IFaultService {

    private final FaultRepository faultRepository;

    public FaultService(FaultRepository faultRepository) {
        this.faultRepository = faultRepository;
    }

    @Override
    public Fault add(Fault fault) {
        if (fault == null)
            throw new NoEntityException();
        if (fault.getId() != null)
            throw new NoEmptyIdException(fault.getId());
        fault.setDeleted(false);
        return faultRepository.save(fault);
    }

    @Override
    public Optional<Fault> getNotDeletedById(Long id) {
        return faultRepository.findByIdAndDeleted(Optional.ofNullable(id).orElseThrow(() -> new EmptyIdException(id)), false);
    }

    @Override
    public Page<Fault> getAllNotDeleted(Pageable pageable) {
        return faultRepository.findAllByDeleted(pageable, false);
    }

    @Override
    public void softDelete(Long id) {
        Fault loadedFault = faultRepository
                .findById(Optional.ofNullable(id).orElseThrow(() -> new EmptyIdException(id)))
                .orElseThrow(() -> new EntityNotFoundException(id.toString()));
        loadedFault.setDeleted(true);
        loadedFault.setEndDate(LocalDate.now());
    }

    @Override
    public Fault update(Fault fault) {
        if (fault == null)
            throw new NoEntityException();
        if (fault.getId() == null)
            throw new EmptyIdException(fault.getId());

        Fault loadedFault = faultRepository.findById(fault.getId()).orElseThrow(() -> new EntityNotFoundException(Long.toString(fault.getId())));
        loadedFault.setName(fault.getName());
        loadedFault.setPoints(fault.getPoints());
        loadedFault.setPenalty(fault.getPenalty());
        return loadedFault;
    }

    @Override
    public List<Fault> searchFaults(String name, Integer minPoints, Integer maxPoints, BigDecimal minPenalty, BigDecimal maxPenalty) {
        return faultRepository.findAllByPointsBetweenAndPenaltyBetweenAndNameContainingIgnoreCaseAndDeleted(minPoints, maxPoints, minPenalty, maxPenalty, name, false);
    }

    @Override
    public List<Fault> getAllByListOfId(List<Long> ids) {
        return faultRepository.findAllByListOfId(ids);
    }
}

package pl.kurs.trafficoffence.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.trafficoffence.event.OnInformPersonEvent;
import pl.kurs.trafficoffence.exception.EmptyIdException;
import pl.kurs.trafficoffence.exception.NoEmptyIdException;
import pl.kurs.trafficoffence.exception.NoEntityException;
import pl.kurs.trafficoffence.exception.PersonHaveBanDrivingLicenseException;
import pl.kurs.trafficoffence.model.Offence;
import pl.kurs.trafficoffence.model.Parameterization;
import pl.kurs.trafficoffence.model.Person;
import pl.kurs.trafficoffence.repository.OffenceRepository;
import pl.kurs.trafficoffence.repository.ParameterizationRepository;
import pl.kurs.trafficoffence.repository.PersonRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OffenceService implements IOffenceService {

    private final String LIMIT_OF_PENALTY = "limitOfPenalty";
    private final String LIMIT_OF_POINTS = "limitOfPoints";

    private final OffenceRepository offenceRepository;
    private final PersonRepository personRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ParameterizationRepository parameterizationRepository;

    public OffenceService(OffenceRepository offenceRepository, PersonRepository personRepository, ApplicationEventPublisher applicationEventPublisher, ParameterizationRepository parameterizationRepository) {
        this.offenceRepository = offenceRepository;
        this.personRepository = personRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.parameterizationRepository = parameterizationRepository;
    }

    @Override
    public Offence add(Offence offence) {
        if (offence == null)
            throw new NoEntityException();
        if (offence.getId() != null)
            throw new NoEmptyIdException(offence.getId());

        List<Parameterization> all = parameterizationRepository.findAll();
        Optional<Parameterization> loadedParamForPenalty = parameterizationRepository.findByKeyParam(LIMIT_OF_PENALTY);
        Optional<Parameterization> loadedParamForPoints = parameterizationRepository.findByKeyParam(LIMIT_OF_POINTS);

        BigDecimal limitOfPenalty = loadedParamForPenalty.map(parameterization -> new BigDecimal(parameterization.getValueParam())).orElseGet(() -> new BigDecimal("9000.00"));
        int limitOFPoints = loadedParamForPoints.map(parameterization -> Integer.parseInt(parameterization.getValueParam())).orElseGet(() -> 20);

        int sumOfPoints = offence.getFaults().stream().map(fault -> fault.getPoints()).reduce((p1, p2) -> p1 + p2).get();
        BigDecimal sumOfPenalty = offence.getFaults().stream().map(fault -> fault.getPenalty()).reduce((p1, p2) -> p1.add(p2)).get();

        offence.setPoints(Integer.min(limitOFPoints,sumOfPoints));
        offence.setPenalty(limitOfPenalty.min(sumOfPenalty));

        Long sumPoints = Optional.ofNullable(offenceRepository.sumPointsByPeselAndTime(offence.getPerson(), offence.getTime().minusYears(1L))).orElse(0L);

        if (sumPoints + offence.getPoints() > 24) {
            Person person = offence.getPerson();
            Optional<LocalDate> dateOfBanDrivingLicense = Optional.ofNullable(person.getDataOfBanDrivingLicense());
            if (dateOfBanDrivingLicense.isPresent()) {
                throw new PersonHaveBanDrivingLicenseException(person.getPesel(), person.getDataOfBanDrivingLicense());
            } else {
                applicationEventPublisher.publishEvent(
                        new OnInformPersonEvent(person, offence.getTime().toLocalDate())
                );
                person.setDataOfBanDrivingLicense(offence.getTime().toLocalDate());
                personRepository.save(person);
            }
        }

        return offenceRepository.save(offence);
    }

    @Override
    public Optional<Offence> get(Long id) {
        return offenceRepository.findById(Optional.ofNullable(id).orElseThrow(() -> new EmptyIdException(id)));
    }

    @Override
    public Page<Offence> getAll(Pageable pageable) {
        return offenceRepository.findAll(pageable);
    }

}

package pl.kurs.trafficoffence.service;

import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.trafficoffence.command.CreateOffenceCommand;
import pl.kurs.trafficoffence.event.OnInformPersonEvent;
import pl.kurs.trafficoffence.exception.EmptyIdException;
import pl.kurs.trafficoffence.exception.NoEmptyIdException;
import pl.kurs.trafficoffence.exception.NoEntityException;
import pl.kurs.trafficoffence.exception.PersonHaveBanDrivingLicenseException;
import pl.kurs.trafficoffence.model.*;
import pl.kurs.trafficoffence.repository.FaultPostedRepository;
import pl.kurs.trafficoffence.repository.OffenceRepository;
import pl.kurs.trafficoffence.repository.ParameterizationRepository;
import pl.kurs.trafficoffence.repository.PersonRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class OffenceService implements IOffenceService {

    private final String LIMIT_OF_PENALTY = "limitOfPenalty";
    private final String LIMIT_OF_POINTS = "limitOfPoints";

    private final OffenceRepository offenceRepository;
    private final PersonRepository personRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ParameterizationRepository parameterizationRepository;

    private final ModelMapper mapper;

    private final IPersonService personService;

    private final IFaultService faultService;

    private final FaultPostedRepository faultPostedRepository;

    public OffenceService(OffenceRepository offenceRepository, PersonRepository personRepository, ApplicationEventPublisher applicationEventPublisher, ParameterizationRepository parameterizationRepository, ModelMapper mapper, IPersonService personService, IFaultService faultService, FaultPostedRepository faultPostedRepository) {
        this.offenceRepository = offenceRepository;
        this.personRepository = personRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.parameterizationRepository = parameterizationRepository;
        this.mapper = mapper;
        this.personService = personService;
        this.faultService = faultService;
        this.faultPostedRepository = faultPostedRepository;
    }

    @Override
    public Offence add(CreateOffenceCommand newOffence) {
        if (newOffence == null)
            throw new NoEntityException();

        Offence offence = mapper.map(newOffence, Offence.class);
        Person loadedPerson = personService.getByPesel(newOffence.getPersonPesel()).get();
        offence.setPerson(loadedPerson);
        List<Fault> allFaultByListOfId = faultService.getAllByListOfId(newOffence.getFaults());

        if (offence.getId() != null)
            throw new NoEmptyIdException(offence.getId());

        Optional<Parameterization> loadedParamForPenalty = parameterizationRepository.findByKeyParam(LIMIT_OF_PENALTY);
        Optional<Parameterization> loadedParamForPoints = parameterizationRepository.findByKeyParam(LIMIT_OF_POINTS);

        BigDecimal limitOfPenalty = loadedParamForPenalty.map(parameterization -> new BigDecimal(parameterization.getValueParam())).orElseGet(() -> new BigDecimal("9000.00"));
        int limitOFPoints = loadedParamForPoints.map(parameterization -> Integer.parseInt(parameterization.getValueParam())).orElseGet(() -> 20);

        int sumOfPoints = allFaultByListOfId.stream().map(Fault::getPoints).reduce(Integer::sum).get();
        BigDecimal sumOfPenalty = allFaultByListOfId.stream().map(Fault::getPenalty).reduce(BigDecimal::add).get();

        offence.setPoints(Integer.min(limitOFPoints, sumOfPoints));
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
        Offence savedOffence = offenceRepository.saveAndFlush(offence);
        Set<FaultPosted> faultsPosted = (
                allFaultByListOfId.stream().map(fault ->
                        new FaultPosted(fault.getName(), fault.getPoints(), fault.getPenalty(), savedOffence)
                ).collect(Collectors.toSet())
        );
        faultPostedRepository.saveAllAndFlush(faultsPosted);

        return savedOffence;
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

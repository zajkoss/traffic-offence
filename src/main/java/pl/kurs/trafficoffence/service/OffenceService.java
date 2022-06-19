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
import pl.kurs.trafficoffence.model.Person;
import pl.kurs.trafficoffence.repository.OffenceRepository;
import pl.kurs.trafficoffence.repository.PersonRepository;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
public class OffenceService implements IOffenceService {

    private final OffenceRepository offenceRepository;
    private final PersonRepository personRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public OffenceService(OffenceRepository offenceRepository, PersonRepository personRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.offenceRepository = offenceRepository;
        this.personRepository = personRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public Offence add(Offence offence) {
        if (offence == null)
            throw new NoEntityException();
        if (offence.getId() != null)
            throw new NoEmptyIdException(offence.getId());
        Long sumPoints = Optional.ofNullable(offenceRepository.sumPointsByPeselAndTime(offence.getPerson(), offence.getTime())).orElse(0L);

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

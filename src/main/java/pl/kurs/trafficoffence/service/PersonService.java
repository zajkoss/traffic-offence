package pl.kurs.trafficoffence.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.trafficoffence.dto.PersonDtoWithOffencesSummary;
import pl.kurs.trafficoffence.exception.BadQueryException;
import pl.kurs.trafficoffence.exception.EmptyPeselNumberException;
import pl.kurs.trafficoffence.exception.NoEmptyIdException;
import pl.kurs.trafficoffence.exception.NoEntityException;
import pl.kurs.trafficoffence.model.Person;
import pl.kurs.trafficoffence.model.QOffence;
import pl.kurs.trafficoffence.model.QPerson;
import pl.kurs.trafficoffence.predicate.PersonPredicatesBuilder;
import pl.kurs.trafficoffence.predicate.SearchCriteria;
import pl.kurs.trafficoffence.repository.PersonRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
@Transactional
public class PersonService implements IPersonService {

    private final PersonRepository personRepository;
    private final EntityManager entityManager;

    public PersonService(PersonRepository personRepository, EntityManager entityManager) {
        this.personRepository = personRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Person> getByPesel(String pesel) {
        return personRepository.findByPesel(Optional.ofNullable(pesel).orElseThrow(() -> new EmptyPeselNumberException(pesel)));
    }

    @Override
    public Person add(Person person) {
        if (person == null)
            throw new NoEntityException();
        if (person.getId() != null)
            throw new NoEmptyIdException(person.getId());
        return personRepository.save(person);
    }

    @Override
    public List<PersonDtoWithOffencesSummary> searchPerson(String name, String surname, String pesel) {
        List<Person> persons = personRepository.findAllByNameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndPesel(name, surname, pesel);
        return persons.stream().map(person -> summaryPersonOffences(person)).collect(Collectors.toList());
    }

    @Override
    public List<PersonDtoWithOffencesSummary> searchPerson(String criteria) {
        PersonPredicatesBuilder builder = new PersonPredicatesBuilder();
        List<SearchCriteria> searchCriteriaForOffence = new ArrayList<>();
        if (criteria != null) {
            Pattern pattern = Pattern.compile("(\\w+)(:|<|>)((\\w+)((\\-(\\d+)\\-(\\d+))?))");
            Matcher matcher = pattern.matcher(criteria + ",");
            while (matcher.find()) {
                if (matcher.group(1).equals("points"))
                    searchCriteriaForOffence.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
                else
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
            }
        }
        BooleanExpression exp = builder.build();

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QPerson qperson = QPerson.person;
        QOffence qOffence = QOffence.offence;

        try {

            int minPoints = searchCriteriaForOffence.stream()
                    .filter(searchCriteria -> isNumeric(searchCriteria.getValue().toString()) && searchCriteria.getOperation().equals(">"))
                    .map(searchCriteria -> (Integer.parseInt(searchCriteria.getValue().toString())))
                    .max(Integer::compare).orElse(0);
            int maxPoints = searchCriteriaForOffence.stream()
                    .filter(searchCriteria -> isNumeric(searchCriteria.getValue().toString()) && searchCriteria.getOperation().equals("<"))
                    .map(searchCriteria -> (Integer.parseInt(searchCriteria.getValue().toString())))
                    .min(Integer::compare).orElse(Integer.MAX_VALUE);
            List<Person> persons = queryFactory
                    .selectFrom(qperson)
                    .where(exp)
                    .where(qperson.pesel.in(
                            queryFactory.selectFrom(qOffence)
                                    .select(qOffence.person.pesel)
                                    .groupBy(qOffence.person.pesel)
                                    .having(qOffence.points.sum().between(minPoints, maxPoints))
                                    .fetch()
                    ))
                    .fetch();

            //select all persons without any offences
            if (minPoints <= 0 && maxPoints > minPoints ) {
                persons.addAll(queryFactory.selectFrom(qperson)
                        .leftJoin(qperson.offences, qOffence)
                        .on(qOffence.person.pesel.eq(qperson.pesel))
                        .where(qOffence.id.isNull())
                        .where(exp)
                        .fetch());
            }

            return persons.stream().map(person -> summaryPersonOffences(person)).collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            if (e.getCause().getCause() != null)
                throw new BadQueryException(e.getCause().getCause().getMessage());
            throw new BadQueryException(e.getMessage());
        }
    }


    private PersonDtoWithOffencesSummary summaryPersonOffences(Person person) {
        Optional<Integer> pointsFor2Years = person.getOffences().stream()
                .filter(offence -> offence.getTime().isAfter(LocalDateTime.now().minusYears(2)))
                .map(offence -> offence.getPoints())
                .reduce((offence, offence2) -> offence + offence2);
        PersonDtoWithOffencesSummary personWithOffences = new PersonDtoWithOffencesSummary();
        personWithOffences.setId(person.getId());
        personWithOffences.setName(person.getName());
        personWithOffences.setLastname(person.getLastname());
        personWithOffences.setPesel(person.getPesel());
        personWithOffences.setTotalOffences(person.getOffences().size());
        personWithOffences.setPoints(pointsFor2Years.orElse(0));
        return personWithOffences;
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
        } catch (final NumberFormatException e) {
            return false;
        }
        return true;
    }
}

package pl.kurs.trafficoffence.validator;

import pl.kurs.trafficoffence.repository.PersonRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PersonEmailUniqueValidator implements ConstraintValidator<PersonEmailUnique,String> {

    private final PersonRepository personRepository;

    public PersonEmailUniqueValidator(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !personRepository.existsByEmail(s);
    }
}

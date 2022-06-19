package pl.kurs.trafficoffence.validator;

import pl.kurs.trafficoffence.repository.PersonRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PersonExistValidator implements ConstraintValidator<PersonExist,String> {


    private final PersonRepository personRepository;

    public PersonExistValidator(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void initialize(PersonExist constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String pesel, ConstraintValidatorContext constraintValidatorContext) {
        return personRepository.existsByPesel(pesel);
    }
}

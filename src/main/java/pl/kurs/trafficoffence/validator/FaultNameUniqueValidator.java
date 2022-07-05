package pl.kurs.trafficoffence.validator;

import pl.kurs.trafficoffence.repository.FaultRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FaultNameUniqueValidator implements ConstraintValidator<FaultNameUnique, String> {

    private final FaultRepository faultRepository;

    public FaultNameUniqueValidator(FaultRepository faultRepository) {
        this.faultRepository = faultRepository;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return faultRepository.findByName(value).isEmpty();
    }
}

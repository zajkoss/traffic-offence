package pl.kurs.trafficoffence.validator;

import pl.kurs.trafficoffence.repository.FaultRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class FaultsExistValidator implements ConstraintValidator<FaultsExist, List<Long>> {

    private FaultRepository faultRepository;

    public FaultsExistValidator(FaultRepository faultRepository) {
        this.faultRepository = faultRepository;
    }

    @Override
    public boolean isValid(List<Long> longs, ConstraintValidatorContext constraintValidatorContext) {
        return faultRepository.findAllByListOfId(longs).size() == longs.size();
    }
}

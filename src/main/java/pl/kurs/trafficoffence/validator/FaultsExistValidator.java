package pl.kurs.trafficoffence.validator;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import pl.kurs.trafficoffence.repository.FaultRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FaultsExistValidator implements ConstraintValidator<FaultsExist, List<Long>> {

    private FaultRepository faultRepository;

    public FaultsExistValidator(FaultRepository faultRepository) {
        this.faultRepository = faultRepository;
    }

    @Override
    public boolean isValid(List<Long> longs, ConstraintValidatorContext constraintValidatorContext) {
        List<Long> notFoundFaults = new ArrayList<>();
        for (Long id : longs) {
            if (!faultRepository.existsById(id)) {
                notFoundFaults.add(id);
            }
        }
        if (!notFoundFaults.isEmpty()) {
            ((HibernateConstraintValidatorContext)constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class))
                    .addMessageParameter("list", notFoundFaults.stream().map(Object::toString).collect(Collectors.joining(",")));
            return false;
        }
        return true;
    }
}

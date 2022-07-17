package pl.kurs.trafficoffence.validator;

import pl.kurs.trafficoffence.command.UpdateFaultCommand;
import pl.kurs.trafficoffence.repository.FaultRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FaultNameUniqueValidatorForType implements ConstraintValidator<FaultNameUnique, UpdateFaultCommand> {

    private final FaultRepository faultRepository;

    public FaultNameUniqueValidatorForType(FaultRepository faultRepository) {
        this.faultRepository = faultRepository;
    }


    @Override
    public boolean isValid(UpdateFaultCommand updateFaultCommand, ConstraintValidatorContext constraintValidatorContext) {
        return !faultRepository.existsByNameAndAndDeletedAndIdNot(updateFaultCommand.getName(), false, updateFaultCommand.getId());
    }
}

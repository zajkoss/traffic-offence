package pl.kurs.trafficoffence.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {FaultNameUniqueValidator.class,FaultNameUniqueValidatorForType.class})
@Target(value = {ElementType.TYPE, ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface FaultNameUnique {

    String message() default "Not unique fault name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

package pl.kurs.trafficoffence.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {PersonEmailUniqueValidator.class})
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface PersonEmailUnique {

    String message() default "Not unique value";;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

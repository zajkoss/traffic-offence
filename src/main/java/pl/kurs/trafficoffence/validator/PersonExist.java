package pl.kurs.trafficoffence.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {PersonExistValidator.class})
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface PersonExist {

    String message() default "Person for given PESEL not exists in system";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

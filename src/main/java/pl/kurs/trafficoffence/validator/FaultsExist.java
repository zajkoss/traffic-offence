package pl.kurs.trafficoffence.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {FaultsExistValidator.class})
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface FaultsExist {

    String message() default "List contains not exists fault";

    String list() default "";


    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

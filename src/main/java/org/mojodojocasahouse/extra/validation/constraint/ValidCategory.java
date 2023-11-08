package org.mojodojocasahouse.extra.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.mojodojocasahouse.extra.validation.CategoriesValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CategoriesValidator.class)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCategory {

    String message() default "Category must be valid";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

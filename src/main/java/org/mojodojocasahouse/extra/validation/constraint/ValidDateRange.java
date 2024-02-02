package org.mojodojocasahouse.extra.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.mojodojocasahouse.extra.validation.DateRangeValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {

    String message() default "Date ranges are invalid!";

    String fromDateField();

    String untilDateField();

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        ValidDateRange[] value();
    }
}

package org.mojodojocasahouse.extra.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.mojodojocasahouse.extra.validation.constraint.ValidDateRange;
import org.springframework.beans.BeanWrapperImpl;

import java.sql.Date;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    public String fromDateField;

    public String untilDateField;

    public void initialize(ValidDateRange constraintAnnotation){
        this.fromDateField = constraintAnnotation.fromDateField();
        this.untilDateField = constraintAnnotation.untilDateField();
    }

    public boolean isValid(Object value,
                           ConstraintValidatorContext context) {

        Date fromDate = (Date) new BeanWrapperImpl(value)
                    .getPropertyValue(fromDateField);
        Date untilDate = (Date) new BeanWrapperImpl(value)
                    .getPropertyValue(untilDateField);

        if(fromDate == null || untilDate == null){
            return true;
        }

        // false if fromDate is greater than untilDate.
        // true otherwise.
        return fromDate.compareTo(untilDate) <= 0;
    }

}

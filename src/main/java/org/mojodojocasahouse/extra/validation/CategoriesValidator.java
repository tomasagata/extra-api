package org.mojodojocasahouse.extra.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.mojodojocasahouse.extra.validation.constraint.ValidCategory;


public class CategoriesValidator implements ConstraintValidator<ValidCategory, String> {

    @Override
    public boolean isValid(String category, ConstraintValidatorContext context) {
        return category != null && category.matches("^[A-Za-z\\d-]{0,50}$");
    }
}

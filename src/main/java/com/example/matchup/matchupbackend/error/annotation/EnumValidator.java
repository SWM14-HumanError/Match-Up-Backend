package com.example.matchup.matchupbackend.error.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<Enum, String> {

    private Enum annotation;

    @Override
    public void initialize(Enum constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // null에 관한 validation은 @NotNull로 진행

        Object[] enumValues = this.annotation.enumClass().getEnumConstants();
        if (enumValues != null) {
            for (Object enumValue : enumValues) {
                if (enumValue.toString().equals(value)
                        || (this.annotation.ignoreCase() && enumValue.toString().equalsIgnoreCase(value))) {
                    return true;
                }
            }
        }
        return false;
    }
}

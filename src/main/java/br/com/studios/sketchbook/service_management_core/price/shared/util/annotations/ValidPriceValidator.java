package br.com.studios.sketchbook.service_management_core.price.shared.util.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class ValidPriceValidator implements ConstraintValidator<ValidPriceInString, String> {

    private static final Pattern PATTERN = Pattern.compile("^\\d+(\\.\\d{1,2})?$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // para @NotBlank cuidar disso
        return PATTERN.matcher(value).matches();
    }
}


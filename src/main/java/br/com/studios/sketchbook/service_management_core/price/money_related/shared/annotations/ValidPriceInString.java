package br.com.studios.sketchbook.service_management_core.price.money_related.shared.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPriceValidator.class)
public @interface ValidPriceInString {
    String message() default "O valor deve ser um número decimal positivo com até 2 casas decimais";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

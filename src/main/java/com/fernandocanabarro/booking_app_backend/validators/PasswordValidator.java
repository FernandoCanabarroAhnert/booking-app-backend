package com.fernandocanabarro.booking_app_backend.validators;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.fernandocanabarro.booking_app_backend.models.dtos.base.PasswordPropertyInterface;
import com.fernandocanabarro.booking_app_backend.models.dtos.exceptions.FieldMessage;
import com.fernandocanabarro.booking_app_backend.validators.annotations.PasswordValid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordValid, PasswordPropertyInterface> {

    @Override
    public void initialize(PasswordValid ann) {}

    @Override
    public boolean isValid(PasswordPropertyInterface value, ConstraintValidatorContext context) {
        List<FieldMessage> errors = new ArrayList<>();

        String password = value.getPassword();

        if (!Pattern.matches(".*[A-Z].*", password)) {
            errors.add(new FieldMessage("password", "Password must contain at least one uppercase letter"));
        }
        if (!Pattern.matches(".*[a-z].*", password)) {
            errors.add(new FieldMessage("password", "Password must contain at least one lowercase letter"));
        }
        if (!Pattern.matches(".*[0-9].*", password)) {
            errors.add(new FieldMessage("password", "Password must contain at least one number"));
        }
        if (!Pattern.matches(".*[\\W].*", password)) {
            errors.add(new FieldMessage("password", "Password must contain at least one special character"));
        }

        errors.forEach(error -> {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(error.getMessage())
                    .addPropertyNode(error.getFieldName())
                    .addConstraintViolation();
        });

        return errors.isEmpty();
    }

    

}

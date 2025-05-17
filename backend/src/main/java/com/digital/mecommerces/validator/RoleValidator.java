package com.digital.mecommerces.validator;

import com.digital.mecommerces.model.Usuario;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class RoleValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Usuario.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Usuario usuario = (Usuario) target;
        if (usuario.getRol() == null) {
            errors.rejectValue("rol", "role.null", "El rol es obligatorio");
        }
    }
}


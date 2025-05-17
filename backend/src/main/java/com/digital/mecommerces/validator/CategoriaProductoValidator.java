package com.digital.mecommerces.validator;

import com.digital.mecommerces.model.CategoriaProducto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CategoriaProductoValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return CategoriaProducto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CategoriaProducto categoria = (CategoriaProducto) target;

        // Validar que el nombre no esté vacío
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            errors.rejectValue("nombre", "nombre.empty", "El nombre de la categoría es obligatorio");
        }

        // Validar que una categoría no sea su propia padre
        if (categoria.getCategoriaPadre() != null &&
                categoria.getCategoriaId() != null &&
                categoria.getCategoriaId().equals(categoria.getCategoriaPadre().getCategoriaId())) {
            errors.rejectValue("categoriaPadre", "categoria.autoReferencia", "Una categoría no puede ser su propia categoría padre");
        }
    }
}


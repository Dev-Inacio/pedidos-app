package com.pedidos.util;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.Getter;

public class ValidadorUtil {
    @Getter
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
}

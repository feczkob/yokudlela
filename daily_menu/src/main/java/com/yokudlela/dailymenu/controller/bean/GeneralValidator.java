package com.yokudlela.dailymenu.controller.bean;


import com.yokudlela.dailymenu.application.error.ErrorCase;
import com.yokudlela.dailymenu.business.error.BusinessException;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.ext.Provider;

@NoArgsConstructor
@AllArgsConstructor
@Provider
public class GeneralValidator<T> {

    @Inject
    Validator validator;


    public void validate(final T object, final Class<?>... groups) {
        Set<ConstraintViolation<T>> validate = validator.validate(object, groups);
        if (!validate.isEmpty()) {
            throw new BusinessException(ErrorCase.VALIDATION_ERROR,
                    (Throwable) validate.stream().map(ConstraintViolation::getMessage));
        }
    }
}

package com.yokudlela.kitchen.controller.validator

import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.beanvalidation.SpringValidatorAdapter
import org.springframework.web.bind.MethodArgumentNotValidException
import javax.validation.Validation

@Component
class PatchedRequestValidator {

    fun validate(
        patchedObject: Any,
        patchedObjectName: String,
        controllerPatchMethodName: String,
        controllerClass: Class<*>,
        vararg controllerPatchMethodParameterTypes: Class<*>
    ) {
        val factory = Validation.buildDefaultValidatorFactory()
        val validator = factory.validator

        val v = SpringValidatorAdapter(validator)
        val errors = BeanPropertyBindingResult(patchedObject, patchedObjectName)
        v.validate(patchedObject, errors)
        if (errors.hasErrors()) {
            throw MethodArgumentNotValidException(
                MethodParameter(
                    controllerClass.getDeclaredMethod(
                        controllerPatchMethodName,
                        *controllerPatchMethodParameterTypes //Int::class.java, objectToPatchClass
                    ), 0
                ),
                errors
            )
        }
    }
}
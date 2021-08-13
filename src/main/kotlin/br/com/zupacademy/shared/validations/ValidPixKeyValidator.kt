package br.com.zupacademy.shared.validations

import br.com.zupacademy.register.NewKeyPix
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton

@Singleton
class ValidPixKeyValidator: ConstraintValidator<ValidPixKey, NewKeyPix> {


    override fun isValid(
        value: NewKeyPix?,
        annotationMetadata: AnnotationValue<ValidPixKey>,
        context: ConstraintValidatorContext
    ): Boolean {
       if (value?.keyType == null) {
           return false
       }

        return value.keyType.validator(value.keyValue)
    }

}

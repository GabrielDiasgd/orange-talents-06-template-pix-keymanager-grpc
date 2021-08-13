package br.com.zupacademy.shared.validations

import java.lang.reflect.Type
import javax.validation.Constraint

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Constraint(validatedBy = [ValidPixKeyValidator::class])
annotation class ValidPixKey(val message: String = "Tipo de chave é incompativel com valor informado")

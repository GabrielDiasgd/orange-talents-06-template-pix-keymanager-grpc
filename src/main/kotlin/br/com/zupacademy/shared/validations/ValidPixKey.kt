package br.com.zupacademy.shared.validations

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Constraint(validatedBy = [ValidPixKeyValidator::class])
annotation class ValidPixKey(
    val message: String = "Tipo de chave (\${validatedValue.keyType}) é incompativel com valor informado (\${validatedValue.keyValue})",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

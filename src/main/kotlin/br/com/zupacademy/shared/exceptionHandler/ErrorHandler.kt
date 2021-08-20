package br.com.zupacademy.shared.exceptionHandler

import io.micronaut.aop.Around

@Around
@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ErrorHandler()

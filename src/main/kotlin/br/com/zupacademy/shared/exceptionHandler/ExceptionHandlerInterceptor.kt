package br.com.zupacademy.shared.exceptionHandler

import br.com.zupacademy.shared.exceptions.ExistingPixKeyException
import br.com.zupacademy.shared.exceptions.PixKeyNotFoundException
import io.grpc.BindableService
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import io.micronaut.http.client.exceptions.HttpClientException
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@InterceptorBean(ErrorHandler::class)
class ExceptionHandlerInterceptor : MethodInterceptor<BindableService, Any?> {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun intercept(context: MethodInvocationContext<BindableService, Any?>): Any? {
        logger.info("Intercepitando o método: ${context.targetMethod.name}")
        return try {
            context.proceed() //Vai invocar o método interceptado, ou seja executar o método normalmente
        } catch (ex: Exception) {

            val statusError = when (ex) {
                is IllegalArgumentException -> Status.INVALID_ARGUMENT.withDescription(ex.message).asRuntimeException()
                is IllegalStateException -> Status.FAILED_PRECONDITION.withDescription(ex.message).asRuntimeException()
                is PixKeyNotFoundException -> Status.NOT_FOUND.withDescription(ex.message).asRuntimeException()
                is ConstraintViolationException -> Status.INVALID_ARGUMENT.withDescription(ex.message).asRuntimeException()
                is ExistingPixKeyException -> Status.ALREADY_EXISTS.withDescription(ex.message).asRuntimeException()
                is HttpClientException -> Status.FAILED_PRECONDITION.withDescription("Erro na criação da chave no banco central").asRuntimeException()
                else -> {
                    logger.error("Ocorreu um erro inesperado na ${ex.javaClass.name}, descricação: ${ex.message}")
                    Status.UNKNOWN.withDescription("Erro inesperado").asRuntimeException()
                }
            }

            val responseObserver = context.parameterValues[1] as StreamObserver<*>
            responseObserver.onError(statusError)
            null
        }
    }
}
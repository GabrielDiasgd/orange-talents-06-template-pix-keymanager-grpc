package br.com.zupacademy.register

import br.com.zupacademy.*
import br.com.zupacademy.shared.exceptions.KeyPixNotFoundException
import io.grpc.Status
import io.grpc.stub.StreamObserver
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class KeyPixGrpcServer(@Inject private val service: RegisterPixKeyService) : KeyManagerServiceGrpc.KeyManagerServiceImplBase() {

    override fun keyRegistration(
        request: PixKeyRegistrationRequest,
        responseObserver: StreamObserver<PixKeyRegistrationResponse>
    ) {

        var createdKey: KeyPix?
        try {
            val newKeyPix = request.toModel()
            createdKey = service.register(newKeyPix)
        }catch (ex: ConstraintViolationException) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription(ex.message)
                .asRuntimeException())
            return
        } catch (ex: IllegalStateException) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription(ex.message)
                .asRuntimeException())
            return
        } catch (ex: KeyPixNotFoundException) {
            responseObserver.onError(Status.ALREADY_EXISTS
                .withDescription(ex.message)
                .asRuntimeException())
            return
        }

        val response = PixKeyRegistrationResponse.newBuilder()
            .setClientId(createdKey.clientId)
            .setPixId(createdKey.pixId)
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}









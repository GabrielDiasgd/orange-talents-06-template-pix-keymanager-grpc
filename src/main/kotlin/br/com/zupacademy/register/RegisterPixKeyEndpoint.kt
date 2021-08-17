package br.com.zupacademy.register

import br.com.zupacademy.*
import br.com.zupacademy.shared.exceptions.ExistingPixKeyException
import br.com.zupacademy.shared.toModel
import io.grpc.Status
import io.grpc.stub.StreamObserver
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class RegisterPixKeyEndpoint(@Inject private val service: RegisterPixKeyService) : KeyManagerRegisterServiceGrpc.KeyManagerRegisterServiceImplBase(){

    override fun register(
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
        } catch (ex: ExistingPixKeyException) {
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









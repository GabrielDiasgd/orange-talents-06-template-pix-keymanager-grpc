package br.com.zupacademy.register

import br.com.zupacademy.KeyManagerRegisterServiceGrpc
import br.com.zupacademy.PixKeyRegistrationRequest
import br.com.zupacademy.PixKeyRegistrationResponse
import br.com.zupacademy.shared.exceptionHandler.ErrorHandler
import br.com.zupacademy.shared.toModel
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class RegisterPixKeyEndpoint(@Inject private val service: RegisterPixKeyService) : KeyManagerRegisterServiceGrpc.KeyManagerRegisterServiceImplBase(){

    @ErrorHandler
    override fun register(
        request: PixKeyRegistrationRequest,
        responseObserver: StreamObserver<PixKeyRegistrationResponse>
    ) {

        println(request)
            val newKeyPix = request.toModel()
            val createdKey = service.register(newKeyPix)

        val response = PixKeyRegistrationResponse.newBuilder()
            .setClientId(createdKey.clientId)
            .setPixId(createdKey.pixId)
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}









package br.com.zupacademy.delete

import br.com.zupacademy.KeyManagerDeleteServiceGrpc
import br.com.zupacademy.PixKeyDeleteRequest
import br.com.zupacademy.PixKeyDeleteResponse
import br.com.zupacademy.shared.exceptionHandler.ErrorHandler
import io.grpc.Status
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@ErrorHandler
@Singleton
class DeletePixKeyEndpoint(@Inject val deleteKeyService: DeleteKeyService) :
    KeyManagerDeleteServiceGrpc.KeyManagerDeleteServiceImplBase() {

    override fun delete(request: PixKeyDeleteRequest, responseObserver: StreamObserver<PixKeyDeleteResponse>) {

        deleteKeyService.delete(request.pixId, request.clientId)

        responseObserver.onNext(PixKeyDeleteResponse.newBuilder().setResponse("Chave excluida com sucesso").build())
        responseObserver.onCompleted()
    }
}


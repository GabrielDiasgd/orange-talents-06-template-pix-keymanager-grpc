package br.com.zupacademy.delete

import br.com.zupacademy.KeyManagerDeleteServiceGrpc
import br.com.zupacademy.PixKeyDeleteRequest
import br.com.zupacademy.PixKeyDeleteResponse
import io.grpc.Status
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class DeletePixKeyEndpoint(@Inject val deleteKeyService: DeleteKeyService) :
    KeyManagerDeleteServiceGrpc.KeyManagerDeleteServiceImplBase() {

    override fun delete(request: PixKeyDeleteRequest, responseObserver: StreamObserver<PixKeyDeleteResponse>) {

        try {
            deleteKeyService.delete(request.pixId, request.clientId)
        } catch (ex: IllegalStateException) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription(ex.message)
                    .asRuntimeException()
            )
        } catch (ex: ConstraintViolationException) {
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription(ex.message)
                    .asRuntimeException()
            )
        }

        responseObserver.onNext(PixKeyDeleteResponse.newBuilder().setResponse("Chave excluida com sucesso").build())
        responseObserver.onCompleted()
    }
}


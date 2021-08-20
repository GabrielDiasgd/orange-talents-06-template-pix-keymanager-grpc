package br.com.zupacademy.list

import br.com.zupacademy.*
import br.com.zupacademy.register.KeyPix
import br.com.zupacademy.register.KeyPixRepository
import br.com.zupacademy.shared.exceptionHandler.ErrorHandler
import com.google.protobuf.Timestamp
import io.grpc.Status
import io.grpc.stub.StreamObserver
import java.lang.IllegalStateException
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class ListPixKeyEndpoint(@Inject private val keyPixRepository: KeyPixRepository) :
    KeyManagerListServiceGrpc.KeyManagerListServiceImplBase() {

    @ErrorHandler
    override fun listKeys(request: ListPixKeyRequest, responseObserver: StreamObserver<ListPixKeyResponse>) {

        if (request.clientId.isNullOrBlank()) throw IllegalArgumentException("Identificador do cliente não pode ser nulo ou vazio")

        val keys = keyPixRepository.findByClientId(request.clientId).map { toListPixKeyResponse(it) }

        responseObserver.onNext(ListPixKeyResponse.newBuilder().addAllListPixKey(keys).build())
        responseObserver.onCompleted()

    }
}

fun toListPixKeyResponse(pixKey: KeyPix): ListPixKeyResponse.ListPixKeyDetails {
    return ListPixKeyResponse.ListPixKeyDetails.newBuilder()
        .setPixId(pixKey.pixId)
        .setClientId(pixKey.clientId)
        .setKeyType(KeyTypeRequest.valueOf(pixKey.type.name))
        .setKeyValue(pixKey.keyValue)
        .setType(AccountType.valueOf(pixKey.typeAccount.name))
        .setCreatedIn(pixKey.createdIn.let {
            val createdIn = it.atZone(ZoneId.of("UTC")).toInstant()
            Timestamp.newBuilder()
                .setSeconds(createdIn.epochSecond)
                .setNanos(createdIn.nano)
                .build()
        }).build()

}
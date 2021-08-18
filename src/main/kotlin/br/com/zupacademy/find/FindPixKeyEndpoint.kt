package br.com.zupacademy.find

import br.com.zupacademy.*
import br.com.zupacademy.integration.bcb.BcbClient
import br.com.zupacademy.register.KeyPixRepository
import br.com.zupacademy.shared.exceptions.PixKeyNotFoundException
import br.com.zupacademy.shared.toModel
import com.google.protobuf.Timestamp
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.validation.validator.Validator
import java.lang.IllegalStateException
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class FindPixKeyEndpoint(
    @Inject val keyPixRepository: KeyPixRepository,
    @Inject val bcbClient: BcbClient,
    @Inject val validator: Validator
) :
    KeyManagerFindServiceGrpc.KeyManagerFindServiceImplBase() {

    override fun findKey(request: FindPixKeyRequest, responseObserver: StreamObserver<FindPixKeyResponse>?) {

        try {
            val filter = request.toModel(validator)
            val pixKeyDetails = filter.filtrate(keyPixRepository, bcbClient)
            responseObserver?.onNext(
                FindPixKeyResponse.newBuilder()
                    .setClientId(pixKeyDetails.clientId)
                    .setPixId(pixKeyDetails.pixId)
                    .setTypeKey(KeyTypeRequest.valueOf(pixKeyDetails.keyType.name))
                    .setKeyValue(pixKeyDetails.keyValue)
                    .setName(pixKeyDetails.nameOwnerAccount)
                    .setCpf(pixKeyDetails.cpfOwnerAccount)
                    .setAccount(
                        AccountResponse.newBuilder()
                            .setNameInstitution(pixKeyDetails.accountInstitution)
                            .setAgency(pixKeyDetails.agencyAccount)
                            .setNumber(pixKeyDetails.accountNumber)
                            .setType(AccountType.valueOf(pixKeyDetails.typeAccount.name))
                    )
                    .setCreatedIn(pixKeyDetails.createdIn.let {
                        val createdIn = it.atZone(ZoneId.of("UTC")).toInstant()
                        Timestamp.newBuilder()
                            .setSeconds(createdIn.epochSecond)
                            .setNanos(createdIn.nano)
                            .build()
                    })
                    .build())
            responseObserver?.onCompleted()
        } catch (ex: Exception) {
            when(ex) {
                is IllegalStateException -> responseObserver?.onError(Status.INVALID_ARGUMENT.withDescription(ex.message).asRuntimeException())
                is ConstraintViolationException -> responseObserver?.onError(Status.INVALID_ARGUMENT.withDescription(ex.message).asRuntimeException())
                is PixKeyNotFoundException -> responseObserver?.onError(Status.NOT_FOUND.withDescription(ex.message).asRuntimeException())
            }
        }
    }
}

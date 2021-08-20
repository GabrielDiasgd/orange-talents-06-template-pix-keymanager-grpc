package br.com.zupacademy.find

import br.com.zupacademy.*
import br.com.zupacademy.integration.bcb.BcbClient
import br.com.zupacademy.register.KeyPixRepository
import br.com.zupacademy.shared.exceptionHandler.ErrorHandler
import br.com.zupacademy.shared.toModel
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Validator

@Singleton
open class FindPixKeyEndpoint(
    @Inject val keyPixRepository: KeyPixRepository,
    @Inject val bcbClient: BcbClient,
    @Inject val validator: Validator
) :
    KeyManagerFindServiceGrpc.KeyManagerFindServiceImplBase() {

    @ErrorHandler
    override fun findKey(request: FindPixKeyRequest, responseObserver: StreamObserver<FindPixKeyResponse>?) {

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

    }
}

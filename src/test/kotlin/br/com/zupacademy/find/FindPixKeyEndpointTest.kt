package br.com.zupacademy.find

import br.com.zupacademy.FindPixKeyRequest
import br.com.zupacademy.FindPixKeyResponse
import br.com.zupacademy.KeyManagerFindServiceGrpc
import br.com.zupacademy.integration.bcb.BcbClient
import br.com.zupacademy.integration.bcb.PixKeyDetailsResponse
import br.com.zupacademy.integration.bcb.register.BcbBankAccountResponse
import br.com.zupacademy.integration.bcb.register.BcbOwnerResponse
import br.com.zupacademy.register.Account
import br.com.zupacademy.register.KeyPix
import br.com.zupacademy.register.KeyPixRepository
import br.com.zupacademy.register.KeyType
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class FindPixKeyEndpointTest(
    private val clientGrpc: KeyManagerFindServiceGrpc.KeyManagerFindServiceBlockingStub,
    private val keyPixRepository: KeyPixRepository
) {

    @field:Inject
    lateinit var bcbClient: BcbClient

    private val keyPix = createKeyPix()

    @BeforeEach
    internal fun setUp() {
        keyPixRepository.deleteAll()
        keyPixRepository.save(keyPix)
    }

    @Test
    internal fun `deve buscar uma chave por pixId e clientId`() {
        val requestGrpc = FindPixKeyRequest.newBuilder()
            .setPixId(FindPixKeyRequest.FilterByPixId
                .newBuilder().setPixId(keyPix.pixId)
                .setClientId(keyPix.clientId)
                .build())
            .build()

        val responseGrpc = clientGrpc.findKey(requestGrpc)

        assertNotNull(responseGrpc)
        assertEquals(keyPix.type.name, responseGrpc.typeKey.name)
        assertEquals(keyPix.keyValue, responseGrpc.keyValue)
        assertEquals(keyPix.pixId, responseGrpc.pixId)

    }

    @Test
    internal fun `nao deve encontrar chave por pixId e clientId quando registro nao existir`() {
        val requestGrpc = FindPixKeyRequest.newBuilder()
            .setPixId(FindPixKeyRequest.FilterByPixId
                .newBuilder().setPixId(UUID.randomUUID().toString())
                .setClientId(keyPix.clientId)
                .build())
            .build()

        val error = assertThrows<StatusRuntimeException> {
            clientGrpc.findKey(requestGrpc)
        }
        with(error) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix não encontrada ou não pertence ao cliente", status.description)
        }
    }

    @Test
    internal fun `deve encontrar uma chave por valor da chave quando registro existir localmente`() {
        val requestGrpc = FindPixKeyRequest.newBuilder()
            .setKey(keyPix.keyValue)
            .build()

        val responseGrpc = clientGrpc.findKey(requestGrpc)

        assertEquals(keyPix.type.name, responseGrpc.typeKey.name)
        assertEquals(keyPix.keyValue, responseGrpc.keyValue)

    }

    @Test
    internal fun `deve encontrar chave por valor da chave quando registro existir somente no bcb`() {
        val requestGrpc = FindPixKeyRequest.newBuilder()
            .setKey("anotherBank@gmail.com")
            .build()
       val bcbResponse = createdPixKeyDetailsReponse()
        Mockito.`when`(bcbClient.findByKeyBcb("anotherBank@gmail.com"))
            .thenReturn(HttpResponse.ok(bcbResponse))

        val responseGrpc: FindPixKeyResponse = clientGrpc.findKey(requestGrpc)

        assertNotNull(responseGrpc)
        assertEquals("", responseGrpc.pixId)
        assertEquals("", responseGrpc.clientId)
        assertEquals(bcbResponse.key, responseGrpc.keyValue)
        assertEquals(bcbResponse.keyType, responseGrpc.typeKey.name)
    }

    @Test
    internal fun `nao deve encontrar a chave por valor da chave no sistema local nem no bcb`() {
        val requestGrpc = FindPixKeyRequest.newBuilder()
            .setKey("notExist@email.com")
            .build()
        Mockito.`when`(bcbClient.findByKeyBcb("notExist@email.com")).thenReturn(HttpResponse.notFound())

        val error = assertThrows<StatusRuntimeException> {
            clientGrpc.findKey(requestGrpc)
        }

        with(error) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave Pix não encontrada no banco central", status.description)
        }
    }

    @Test
    internal fun `nao deve encontrar chave quando filtro invalido`() {
       val requestGrpc = FindPixKeyRequest.newBuilder()
           .build()

        val error = assertThrows<StatusRuntimeException> {
            clientGrpc.findKey(requestGrpc)
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Chave Pix inválida ou não informada", status.description)
        }
    }




    @MockBean(BcbClient::class)
    fun bcbClientMock(): BcbClient {
        return Mockito.mock(BcbClient::class.java)
    }
}
@Factory
class ClientGrpc() {
    @Singleton
    fun blokingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerFindServiceGrpc.KeyManagerFindServiceBlockingStub? {
        return KeyManagerFindServiceGrpc.newBlockingStub(channel)
    }
}

fun createKeyPix(): KeyPix {
    return KeyPix(UUID.randomUUID().toString(),KeyType.EMAIL,"gabriel@gmail.com","0001", "44719190839",
    "Itau", "Gabriel", "123456", Account.CONTA_CORRENTE)
}

fun  createdPixKeyDetailsReponse(): PixKeyDetailsResponse {
    return PixKeyDetailsResponse(KeyType.EMAIL.name,"anotherBank@gmail.com",
        BcbBankAccountResponse("Itau","0001", "123456", "CACC"),
    BcbOwnerResponse("NATURAL_PERSON", "Gabriel", "44719190839"), LocalDateTime.now())
}
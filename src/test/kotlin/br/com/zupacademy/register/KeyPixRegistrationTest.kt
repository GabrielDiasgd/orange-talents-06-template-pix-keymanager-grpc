package br.com.zupacademy.register

import br.com.zupacademy.*
import br.com.zupacademy.integration.bcb.BcbClient
import br.com.zupacademy.integration.bcb.register.BCBRegisterKeyRequest
import br.com.zupacademy.integration.bcb.register.BcbBankAccountResponse
import br.com.zupacademy.integration.bcb.register.BcbOwnerResponse
import br.com.zupacademy.integration.bcb.register.BcbRegisterKeyResponse
import br.com.zupacademy.integration.itau.ItauClient
import br.com.zupacademy.integration.itau.responses.AccountResponse
import br.com.zupacademy.integration.itau.responses.InstitutionResponse
import br.com.zupacademy.integration.itau.responses.OwnerAccount
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class KeyPixRegistrationTest(
    private val keyPixRepository: KeyPixRepository,
    private val clientGrpc: KeyManagerRegisterServiceGrpc.KeyManagerRegisterServiceBlockingStub
) {

    /**
     * 1. Happy path
     * 2. Quando já existe chave igual cadastrada
     * 3. chave com valor incompativel
     * 4. cliente não tem conta no itau
     * 5. Erro no banco central
     */

    @field:Inject
    lateinit var itauClient: ItauClient

    @field:Inject
    lateinit var bcbClient: BcbClient

    @BeforeEach
    fun setup() {
        keyPixRepository.deleteAll()
    }

    @Test
    fun `deve cadastrar um nova chave pix`() {
        //cenário
        val request = createRequestGrpc()
        val accountResponse = createAccountResponseItau()
        val keyPix = createKeyPix()
        val bcbResponse = createBcbResponse()

        Mockito.`when`(itauClient.findClient(request.clientId, request.account.name)).thenReturn(HttpResponse.ok(accountResponse))

        Mockito.`when`(bcbClient.registerKeyBcb(BCBRegisterKeyRequest(keyPix))).thenReturn(HttpResponse.created(bcbResponse))
        //ação
        val response = clientGrpc.register(request)

        //validação
        assertEquals("c56dfef4-7901-44fb-84e2-a2cefb157890", response.clientId)
        assertEquals(1, keyPixRepository.count())
    }

    @Test
    internal fun `nao deve cadastrar quando chave ja existente`() {
        //cenário
        val existingKey = createKeyPix()
        keyPixRepository.save(existingKey)
        val request = createRequestGrpc()
        //ação
        val error = assertThrows<StatusRuntimeException> {
            clientGrpc.register(request)
        }
        //validação
        with(error) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Já existe uma chave 44719190839 cadastrada", status.description)
            assertEquals(1, keyPixRepository.count())
        }
    }

    @Test
    internal fun `nao deve cadastrar chave com valor incompativel`() {
        //cenário
        val keyType = KeyTypeRequest.CPF
        val keyValue = "4471919083977777"
        val request = PixKeyRegistrationRequest.newBuilder()
            .setClientId("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setKeyType(keyType)
            .setKeyValue(keyValue)
            .setAccount(AccountType.CONTA_CORRENTE)
            .build()
        //ação
        val error = assertThrows<StatusRuntimeException> {
            clientGrpc.register(request)
        }
        //validação
        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("register.newKeyPix: Tipo de chave ($keyType) é incompativel com valor informado ($keyValue)", status.description)
            assertEquals(0, keyPixRepository.count())
        }
    }

    @Test
    internal fun `nao deve cadastrar chave quando cliente nao possui conta no itau`() {
        //cenário
        val request = PixKeyRegistrationRequest.newBuilder()
            .setClientId("c56dfef4-7901-44fb-84e2-a2cefb157891")
            .setKeyType(KeyTypeRequest.CPF)
            .setKeyValue("44719190839")
            .setAccount(AccountType.CONTA_CORRENTE)
            .build()

        Mockito.`when`(itauClient.findClient(request.clientId, request.account.name))
            .thenReturn(HttpResponse.notFound())
        val error = assertThrows<StatusRuntimeException> {
           clientGrpc.register(request)
        }
        //validação
        with(error) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Conta inexistente", status.description)
            assertEquals(0, keyPixRepository.count())
        }
    }

    @Test
    internal fun `nao deve cadastrar chave quando ocorrer algum erro com o servico do BCB`() {
        val request = createRequestGrpc()
        val accountResponse = createAccountResponseItau()
        val keyPix = createKeyPix()
        Mockito.`when`(itauClient.findClient(request.clientId, request.account.name)).thenReturn(HttpResponse.ok(accountResponse))

        Mockito.`when`(bcbClient.registerKeyBcb(BCBRegisterKeyRequest(keyPix))).thenThrow(HttpClientResponseException::class.java)
        //ação
        val error = assertThrows<StatusRuntimeException> {
            clientGrpc.register(request)
        }
        //validação
        assertEquals(Status.FAILED_PRECONDITION.code, error.status.code)
        assertEquals("Erro na criação da chave no banco central", error.status.description)
        assertEquals(0, keyPixRepository.count())

    }

    @MockBean(ItauClient::class)
    fun accountClientMock(): ItauClient? {
        return Mockito.mock(ItauClient::class.java)
    }

    @MockBean(BcbClient::class)
    fun  bcbClientMock(): BcbClient? {
        return Mockito.mock(BcbClient::class.java)
    }
}

@Factory
class ClientGrpc() {
    @Singleton
    fun blockStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRegisterServiceGrpc.KeyManagerRegisterServiceBlockingStub? {
        return KeyManagerRegisterServiceGrpc.newBlockingStub(channel)
    }
}

fun createRequestGrpc(): PixKeyRegistrationRequest {
   return PixKeyRegistrationRequest.newBuilder()
        .setClientId("c56dfef4-7901-44fb-84e2-a2cefb157890")
        .setKeyType(KeyTypeRequest.CPF)
        .setKeyValue("44719190839")
        .setAccount(AccountType.CONTA_CORRENTE)
        .build()
}

fun createAccountResponseItau(): AccountResponse {
    return AccountResponse("CONTA_CORRENTE", "123456", "0002", InstitutionResponse("Itau", "60701190"),
        OwnerAccount("c56dfef4-7901-44fb-84e2-a2cefb157890", "Gabriel", "44719180939"))
}

fun createKeyPix(): KeyPix {
   return KeyPix("c56dfef4-7901-44fb-84e2-a2cefb157890", KeyType.CPF, "44719190839", "0002",
        "44719190839", "Itau", "Gabriel", "123456", Account.CONTA_CORRENTE)
}

fun createBcbResponse(): BcbRegisterKeyResponse {
    return BcbRegisterKeyResponse("CPF", "44719190839",
        BcbBankAccountResponse(KeyPix.ITAU_BANCO_ISPB, "0002", "123456", "CONTA_CORRENTE"),
        BcbOwnerResponse("NATURAL_PERSON","Gabriel", "44719190839"), LocalDateTime.now())
}
package br.com.zupacademy.register

import br.com.zupacademy.*
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
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
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
     * 5. happy path chave aleatória
     */

    @field:Inject
    lateinit var itauClient: ItauClient

    @BeforeEach
    fun setup() {
        keyPixRepository.deleteAll()
    }

    @Test
    fun `deve cadastrar um nova chave pix`() {
        //cenário
        val request = PixKeyRegistrationRequest.newBuilder()
            .setClientId("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setKeyType(KeyTypeRequest.CPF)
            .setKeyValue("44719190839")
            .setAccount(AccountType.CONTA_CORRENTE)
            .build()
        //ação
        val accountResponse = AccountResponse(
            "CONTA_CORRENTE",
            "1000",
            "0002",
            InstitutionResponse("Itau", "1234"),
            OwnerAccount("c56dfef4-7901-44fb-84e2-a2cefb157890", "Gabriel", "44719180939")
        )
        Mockito.`when`(itauClient.findClient(request.clientId, request.account.name))
            .thenReturn(HttpResponse.ok(accountResponse))
        val response = clientGrpc.register(request)
        //validação
        assertEquals("c56dfef4-7901-44fb-84e2-a2cefb157890", response.clientId)
        assertEquals(1, keyPixRepository.count())
    }

    @Test
    internal fun `nao deve cadastrar quando chave ja existente`() {
        //cenário
        val existingKey = KeyPix(
            "c56dfef4-7901-44fb-84e2-a2cefb157890", KeyType.CPF, "44719190839", "0002",
            "4471919839", "Itau", "Gabriel", "1000", Account.CONTA_CORRENTE
        )
        keyPixRepository.save(existingKey)

        val request = PixKeyRegistrationRequest.newBuilder()
            .setClientId("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setKeyType(KeyTypeRequest.CPF)
            .setKeyValue("44719190839")
            .setAccount(AccountType.CONTA_CORRENTE)
            .build()
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
        val request = PixKeyRegistrationRequest.newBuilder()
            .setClientId("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setKeyType(KeyTypeRequest.CPF)
            .setKeyValue("4471919083977777")
            .setAccount(AccountType.CONTA_CORRENTE)
            .build()
        //ação
        val error = assertThrows<StatusRuntimeException> {
            clientGrpc.register(request)
        }
        //validação
        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("register.newKeyPix: Tipo de chave é incompativel com valor informado", status.description)
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
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Conta inexistente", status.description)
            assertEquals(0, keyPixRepository.count())
        }
    }



    @MockBean(ItauClient::class)
    fun accountClientMock(): ItauClient? {
        return Mockito.mock(ItauClient::class.java)
    }
}

@Factory
class ClientGrpc() {
    @Singleton
    fun blockStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRegisterServiceGrpc.KeyManagerRegisterServiceBlockingStub? {
        return KeyManagerRegisterServiceGrpc.newBlockingStub(channel)
    }
}

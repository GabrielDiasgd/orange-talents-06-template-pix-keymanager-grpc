package br.com.zupacademy.list

import br.com.zupacademy.KeyManagerListServiceGrpc
import br.com.zupacademy.ListPixKeyRequest
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
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList


@MicronautTest(transactional = false)
internal class ListPixKeyEndpointTest(
    @Inject private val keyPixRepository: KeyPixRepository,
    @Inject private val clientGrpc: KeyManagerListServiceGrpc.KeyManagerListServiceBlockingStub
) {

    private val clientId = UUID.randomUUID().toString()

    @BeforeEach
    internal fun setUp() {
        keyPixRepository.deleteAll()
        keyPixRepository.saveAll(listPixKey(clientId))

    }


    @Test
    internal fun `deve listar todas as chaves do cliente quando cliente existente`() {
        //Cenário
        val requestGrpc = ListPixKeyRequest.newBuilder().setClientId(clientId).build()
        //Ação
        val response = clientGrpc.listKeys(requestGrpc)
        //Validação
        assertEquals(response.listPixKeyList.size, 3 )
        assertEquals(response.listPixKeyList[0].keyType.name, KeyType.EMAIL.name )
        assertEquals(response.listPixKeyList[1].keyType.name, KeyType.RANDOM.name )
        assertEquals(response.listPixKeyList[2].keyType.name, KeyType.PHONE.name)

    }

    @Test
    internal fun `deve retornar uma lista de chaves vazia quando cliente nao possui chaves cadastradas`() {
        //Cenário
        val requestGrpc = ListPixKeyRequest.newBuilder().setClientId(UUID.randomUUID().toString()).build()
        //Ação
        val response = clientGrpc.listKeys(requestGrpc)
        //Validação
        assertEquals(response.listPixKeyList.size, 0)
    }

    @Test
    internal fun `nao deve listar chaves do cliente caso cliente seja enviado nulo ou vazio`() {
        //Cenário
        val requestGrpc = ListPixKeyRequest.newBuilder().setClientId("").build()

        //Ação
        val error = assertThrows<StatusRuntimeException> {
            clientGrpc.listKeys(requestGrpc)
        }
        //Validação
        assertEquals(Status.INVALID_ARGUMENT.code, error.status.code)
        assertEquals("Identificador do cliente não pode ser nulo ou vazio", error.status.description)
    }

    @Factory
    class ClientGrpc() {
        @Singleton
        fun blockStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerListServiceGrpc.KeyManagerListServiceBlockingStub? {
            return KeyManagerListServiceGrpc.newBlockingStub(channel)
        }
    }
}



fun listPixKey(clientId: String): ArrayList<KeyPix> {
    return arrayListOf<KeyPix>(KeyPix(
        clientId, KeyType.EMAIL,"gabriel@gmail.com","0001", "44719190839",
        "Itau", "Gabriel", "123456", Account.CONTA_CORRENTE),
        KeyPix(clientId, KeyType.RANDOM,UUID.randomUUID().toString(),"0001", "44719190839",
        "Itau", "Gabriel", "123456", Account.CONTA_POUPANCA),
        KeyPix(clientId, KeyType.PHONE,"+55189810181020","0001", "44719190839",
            "Itau", "Gabriel", "123456", Account.CONTA_CORRENTE),
        KeyPix(UUID.randomUUID().toString(), KeyType.RANDOM,UUID.randomUUID().toString(),"0001", "44719190839",
            "Itau", "Gabriel", "123456", Account.CONTA_CORRENTE))
}
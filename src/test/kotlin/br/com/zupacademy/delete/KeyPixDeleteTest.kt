package br.com.zupacademy.delete

import br.com.zupacademy.KeyManagerDeleteServiceGrpc
import br.com.zupacademy.PixKeyDeleteRequest
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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
class KeyPixDeleteTest(
    @Inject private val clientGrpc: KeyManagerDeleteServiceGrpc.KeyManagerDeleteServiceBlockingStub,
    @Inject val keyPixRepository: KeyPixRepository
) {
    private val existingKey = KeyPix(
        "c56dfef4-7901-44fb-84e2-a2cefb157890", KeyType.CPF, "44719190839", "0002",
        "4471919839", "Itau", "Gabriel", "1000", Account.CONTA_CORRENTE)

    @BeforeEach
    internal fun setup() {
        keyPixRepository.deleteAll()
        keyPixRepository.save(existingKey)
    }

    @Test
    internal fun `deve deletar chave quando informado valores corretos`() {
        //cenário
        val request = PixKeyDeleteRequest.newBuilder().setPixId(existingKey.pixId).setClientId(existingKey.clientId).build()
        //ação
        val response = clientGrpc.delete(request)
        //validação
        assertEquals("Chave excluida com sucesso", response.response)
        assertEquals(0, keyPixRepository.count())
    }

    @Test
    internal fun `nao deve deletar chave quando chave nao encontrada`() {
        //cenário
        val request = PixKeyDeleteRequest.newBuilder().setPixId("chave inexistente").setClientId(existingKey.clientId).build()
        //ação
        val error = assertThrows<StatusRuntimeException> {
            clientGrpc.delete(request)
        }
        //validação
        assertEquals("Chave pix não encontrada", error.status.description)
        assertEquals(Status.NOT_FOUND.code, error.status.code)
    }

    @Test
    internal fun `nao deve deletar chave quando a chave nao pertencer ao cliente`() {
        //cenário
        val request = PixKeyDeleteRequest.newBuilder().setPixId(existingKey.pixId).setClientId("cliente diferente").build()
        //ação
        val error = assertThrows<StatusRuntimeException> {
            clientGrpc.delete(request)
        }
        //validação
        assertEquals("Chave pix não pertence ao cliente", error.status.description)
        assertEquals(Status.NOT_FOUND.code, error.status.code)
    }

    @Test
    internal fun `nao deve deletar chave quando valores nulos na requisicao`() {
        //cenário
        val request = PixKeyDeleteRequest.newBuilder().setPixId("").setClientId("").build()!!
        println(request)
        //ação
        val error = assertThrows<StatusRuntimeException> {
            clientGrpc.delete(request)
        }
        //validação
        assertEquals(Status.INVALID_ARGUMENT.code, error.status.code)
    }
}

@Factory
class ClientGrpc() {
    @Singleton
    fun blockStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerDeleteServiceGrpc.KeyManagerDeleteServiceBlockingStub? {
        return KeyManagerDeleteServiceGrpc.newBlockingStub(channel)
    }
}

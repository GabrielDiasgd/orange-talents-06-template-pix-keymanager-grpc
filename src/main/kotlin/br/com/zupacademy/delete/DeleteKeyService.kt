package br.com.zupacademy.delete

import br.com.zupacademy.integration.bcb.BcbClient
import br.com.zupacademy.integration.bcb.BcbDeleteKeyRequest
import br.com.zupacademy.register.KeyPixRepository
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class DeleteKeyService(@Inject val keyPixRepository: KeyPixRepository, @Inject val bcbClient: BcbClient) {

    @Transactional
    fun delete(@NotBlank pixId: String, @NotBlank clientId: String) {
        val keyPix = keyPixRepository.findById(pixId)
        keyPix.orElseThrow {throw  IllegalStateException("Chave pix não encontrada")}

        keyPix.get().belongsClient(clientId).run {
            if (!this) throw IllegalStateException("Chave pix não pertence ao cliente")
        }
        keyPixRepository.deleteById(keyPix.get().pixId)

        val bcbResponse = bcbClient.deleteKeyBcb(keyPix.get().keyValue, BcbDeleteKeyRequest(keyPix.get().keyValue))
        if (bcbResponse.status != HttpStatus.OK) throw IllegalStateException("Não foi possível excluir a chave no Banco Central")

    }


}
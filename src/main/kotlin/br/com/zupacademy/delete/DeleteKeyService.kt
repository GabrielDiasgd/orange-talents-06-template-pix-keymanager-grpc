package br.com.zupacademy.delete

import br.com.zupacademy.register.KeyPixRepository
import io.grpc.Status
import io.micronaut.validation.Validated
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class DeleteKeyService(@Inject val keyPixRepository: KeyPixRepository) {

    @Transactional
    fun delete(@NotBlank pixId: String, @NotBlank clientId: String) {
        val keyPix = keyPixRepository.findById(pixId)
        keyPix.orElseThrow {throw  IllegalStateException("Chave pix não encontrada")}

        keyPix.get().belongsClient(clientId).run {
            if (!this) throw IllegalStateException("Chave pix não pertence ao cliente")
        }
        keyPixRepository.deleteById(keyPix.get().pixId)
    }


}
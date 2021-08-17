package br.com.zupacademy.register

import br.com.zupacademy.integration.bcb.*
import br.com.zupacademy.integration.bcb.register.BCBRegisterKeyRequest
import br.com.zupacademy.integration.itau.ItauClient
import br.com.zupacademy.shared.exceptions.ExistingPixKeyException
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Singleton
@Validated
class RegisterPixKeyService(
    @Inject val keyPixRepository: KeyPixRepository,
    @Inject val itauClient: ItauClient,
    @Inject val bcbClient: BcbClient
) {

    @Transactional
    fun register(@Valid newKeyPix: NewKeyPix): KeyPix {

        if (keyPixRepository.existsByKeyValue(newKeyPix.keyValue)) {
            throw ExistingPixKeyException("Já existe uma chave ${newKeyPix.keyValue} cadastrada")
        }
        val response = itauClient.findClient(newKeyPix.clientId, newKeyPix.account!!.name)
        response.body() ?: throw IllegalStateException("Conta inexistente")

        val keyPix = newKeyPix.toModel(response.body())
        keyPixRepository.save(keyPix)

        val bcbResponse = bcbClient.registerKeyBcb(BCBRegisterKeyRequest(keyPix))
        if (bcbResponse.status != HttpStatus.CREATED) throw IllegalStateException("Erro na criação da chave no banco central")

        val updatedKey = keyPix.updateKey(bcbResponse.body().key)

        return keyPixRepository.update(updatedKey)
    }

}
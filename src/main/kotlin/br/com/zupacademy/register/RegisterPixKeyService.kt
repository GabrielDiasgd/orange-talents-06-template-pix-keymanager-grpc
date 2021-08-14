package br.com.zupacademy.register

import br.com.zupacademy.integration.ClientsAccountsItauClient
import br.com.zupacademy.shared.exceptions.ExistingPixKeyException
import io.micronaut.validation.Validated
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Singleton
@Validated
class RegisterPixKeyService(@Inject val keyPixRepository: KeyPixRepository, @Inject val itauClient: ClientsAccountsItauClient) {

    @Transactional
    fun register(@Valid newKeyPix: NewKeyPix): KeyPix {

        if (keyPixRepository.existsByKeyValue(newKeyPix.keyValue)) {
            throw ExistingPixKeyException("Já existe uma chave ${newKeyPix.keyValue} cadastrada")
        }

        val response = itauClient.findClient(newKeyPix.clientId, newKeyPix.account!!.name)
        response.body() ?: throw IllegalStateException("Conta inexistente")

        val keyPix = newKeyPix.toModel(response.body())
        return  keyPixRepository.save(keyPix)
    }

}
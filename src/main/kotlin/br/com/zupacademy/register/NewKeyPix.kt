package br.com.zupacademy.register

import br.com.zupacademy.integration.itau.responses.AccountResponse
import br.com.zupacademy.shared.validations.ValidPixKey
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPixKey
@Introspected
data class NewKeyPix(
    @field:NotBlank
    val clientId: String,
    @field:NotNull
    val keyType: KeyType?,
    @field:Size(max = 77)
    val keyValue: String,
    @field:NotNull
    val account: Account?
) {
    fun toModel(accountResponse: AccountResponse): KeyPix {
        return KeyPix(
            accountResponse.ownerAccount.id,
            KeyType.valueOf(keyType!!.name),
            if (keyValue.isNullOrBlank()) UUID.randomUUID().toString() else keyValue,
            accountResponse.agency,
            accountResponse.ownerAccount.cpf,
            accountResponse.institution.name,
            accountResponse.ownerAccount.name,
            accountResponse.number,
            Account.valueOf(accountResponse.type)
        )
    }
}

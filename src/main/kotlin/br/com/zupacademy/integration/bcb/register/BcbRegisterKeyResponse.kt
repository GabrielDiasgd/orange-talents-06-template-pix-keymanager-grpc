package br.com.zupacademy.integration.bcb.register

import br.com.zupacademy.find.PixKeyDetails
import br.com.zupacademy.register.Account
import br.com.zupacademy.register.KeyType
import java.time.LocalDateTime

data class BcbRegisterKeyResponse (
    val keyType: String,
    val key: String,
    val bankAccount: BcbBankAccountResponse,
    val owner: BcbOwnerResponse,
    val createdAt: LocalDateTime
    ) {
    override fun toString(): String {
        return "BcbRegisterKeyResponse(keyType='$keyType', key='$key', bankAccount=$bankAccount, owner=$owner, createdAt=$createdAt)"
    }

}
    class BcbBankAccountResponse(
        val participant: String,
        val branch: String,
        val accountNumber: String,
        val accountType: String,
    ) {

    }

    class BcbOwnerResponse(
        val type: String,
        val name: String,
        val taxIdNumber: String
    )

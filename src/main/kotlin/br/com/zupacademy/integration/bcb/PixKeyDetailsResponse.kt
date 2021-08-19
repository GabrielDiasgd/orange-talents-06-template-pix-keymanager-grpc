package br.com.zupacademy.integration.bcb

import br.com.zupacademy.find.PixKeyDetails
import br.com.zupacademy.integration.bcb.register.BcbBankAccountResponse
import br.com.zupacademy.integration.bcb.register.BcbOwnerResponse
import br.com.zupacademy.register.Account
import br.com.zupacademy.register.KeyType
import java.time.LocalDateTime

data class PixKeyDetailsResponse(
    val keyType: String,
    val key: String,
    val bankAccount: BcbBankAccountResponse,
    val owner: BcbOwnerResponse,
    val createdAt: LocalDateTime
) {

    fun toPixKeyDetails(): PixKeyDetails {
        return PixKeyDetails(
            "",
            "",
            KeyType.valueOf(keyType),
            key,
            bankAccount.branch,
            owner.taxIdNumber,
            bankAccount.participant,
            owner.name,
            bankAccount.accountNumber,
            when (bankAccount.accountType) {
                "CACC" -> Account.CONTA_CORRENTE
                else -> Account.CONTA_POUPANCA
            },
            createdAt
        )
    }
}

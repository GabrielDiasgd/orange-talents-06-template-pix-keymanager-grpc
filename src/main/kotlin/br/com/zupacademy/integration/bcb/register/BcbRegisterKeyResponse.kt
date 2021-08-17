package br.com.zupacademy.integration.bcb.register

import java.time.LocalDateTime

data class BcbRegisterKeyResponse (
    val keyType: String,
    val key: String,
    val bankAccount: BcbBankAccountResponse,
    val owner: BcbOwnerResponse,
    val createdAt: LocalDateTime
    )
{}


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
){}
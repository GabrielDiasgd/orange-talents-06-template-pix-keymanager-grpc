package br.com.zupacademy.integration.bcb.register

import br.com.zupacademy.register.KeyPix


class BCBRegisterKeyRequest(keyPix: KeyPix){
    val keyType = keyPix.type
    val key = keyPix.keyValue
    val bankAccount = BcbBankAccountRequest(keyPix)
    val owner = BcbOwnerRequest(keyPix)
}


class BcbBankAccountRequest(keyPix: KeyPix) {
    val participant = KeyPix.ITAU_BANCO_ISPB
    val branch = keyPix.agencyAccount
    val accountNumber = keyPix.accountNumber
    val accountType = when (keyPix.typeAccount.name){
        "CONTA_CORRENTE" -> BcbAccountType.CACC
         else -> BcbAccountType.SVGS
    }
}

class BcbOwnerRequest(keyPix: KeyPix){
    val type = TypePerson.NATURAL_PERSON
    val name = keyPix.nameOwnerAccount
    val taxIdNumber = keyPix.cpfOwnerAccount
}

enum class TypePerson {
    NATURAL_PERSON, LEGAL_PERSON
}
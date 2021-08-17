package br.com.zupacademy.integration.bcb.register

import br.com.zupacademy.register.KeyPix


class BCBRegisterKeyRequest(keyPix: KeyPix){
    val keyType = keyPix.type
    val key = keyPix.keyValue
    val bankAccount = BcbBankAccountRequest(keyPix)
    val owner = BcbOwnerRequest(keyPix)


    override fun toString(): String {
        return "BCBRegisterKeyRequest(keyType=$keyType, key='$key', bankAccount=$bankAccount, owner=$owner)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BCBRegisterKeyRequest

        if (keyType != other.keyType) return false

        return true
    }

    override fun hashCode(): Int {
        return keyType.hashCode()
    }


}


class BcbBankAccountRequest(keyPix: KeyPix) {
    val participant = KeyPix.ITAU_BANCO_ISPB
    val branch = keyPix.agencyAccount
    val accountNumber = keyPix.accountNumber
    val accountType = when (keyPix.typeAccount.name){
        "CONTA_CORRENTE" -> BcbAccountType.CACC
         else -> BcbAccountType.SVGS
    }

    override fun toString(): String {
        return "BcbBankAccountRequest(participant='$participant', branch='$branch', accountNumber='$accountNumber', accountType=$accountType)"
    }


}

class BcbOwnerRequest(keyPix: KeyPix){
    val type = TypePerson.NATURAL_PERSON
    val name = keyPix.nameOwnerAccount
    val taxIdNumber = keyPix.cpfOwnerAccount
    override fun toString(): String {
        return "BcbOwnerRequest(type=$type, name='$name', taxIdNumber='$taxIdNumber')"
    }


}

enum class TypePerson {
    NATURAL_PERSON, LEGAL_PERSON
}
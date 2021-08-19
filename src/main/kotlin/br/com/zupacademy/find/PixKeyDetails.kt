package br.com.zupacademy.find

import br.com.zupacademy.register.Account
import br.com.zupacademy.register.KeyPix
import br.com.zupacademy.register.KeyType
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class PixKeyDetails(
    val pixId: String? = null,
    val clientId: String? = null,
    val keyType: KeyType,
    val keyValue: String,
    val agencyAccount: String,
    val cpfOwnerAccount: String,
    val accountInstitution: String,
    val nameOwnerAccount: String,
    val accountNumber: String,
    val typeAccount: Account,
    val createdIn: LocalDateTime
) {

    companion object {
        fun of (keyPix: KeyPix): PixKeyDetails {
            return PixKeyDetails(
                keyPix.pixId,
                keyPix.clientId,
                keyPix.type,
                keyPix.keyValue,
                keyPix.agencyAccount,
                keyPix.cpfOwnerAccount,
                keyPix.accountInstitution,
                keyPix.nameOwnerAccount,
                keyPix.accountNumber,
                keyPix.typeAccount,
                keyPix.createdIn
            )
        }
    }

    override fun toString(): String {
        return "PixKeyDetails(pixId=$pixId, clientId=$clientId, keyType=$keyType, keyValue='$keyValue', agencyAccount='$agencyAccount', cpfOwnerAccount='$cpfOwnerAccount', accountInstitution='$accountInstitution', nameOwnerAccount='$nameOwnerAccount', accountNumber='$accountNumber', typeAccount=$typeAccount)"
    }
}
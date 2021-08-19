package br.com.zupacademy.register

import br.com.zupacademy.AccountType
import br.com.zupacademy.KeyTypeRequest
import br.com.zupacademy.ListPixKeyResponse
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class KeyPix(
    @field:NotNull @Column(nullable = false)
    val clientId: String,
    @field:NotNull @Enumerated(EnumType.STRING)
    val type: KeyType,
    @field:NotBlank @Column(nullable = false, unique = true) @Size(max = 77)
    val keyValue: String,
    @field:NotBlank @Column(nullable = false)
    val agencyAccount: String,
    @field:NotBlank @Column(nullable = false)
    val cpfOwnerAccount: String,
    @field:NotBlank @Column(nullable = false)
    val accountInstitution: String,
    @field:NotBlank @Column(nullable = false)
    val nameOwnerAccount: String,
    @field:NotBlank @Column(nullable = false)
    val accountNumber: String,
    @field:NotNull @Enumerated(EnumType.STRING)
    val typeAccount: Account

) {
    @Id
    var pixId: String = UUID.randomUUID().toString()
    @Column(nullable = false)
    val createdIn: LocalDateTime = LocalDateTime.now()

    private constructor(key: KeyPix, keyValue: String) : this(key.clientId, key.type, keyValue, key.agencyAccount,
        key.cpfOwnerAccount, key.accountInstitution, key.nameOwnerAccount, key.accountNumber, key.typeAccount){
        this.pixId = key.pixId
    }
    companion object {
        const val ITAU_BANCO_ISPB = "60701190"
    }

    fun belongsClient(clientId: String): Boolean {
        return this.clientId == clientId
    }

    fun updateKey(key: String): KeyPix = KeyPix(this, key)

    override fun toString(): String {
        return "KeyPix(clientId='$clientId', KeyType=$type, keyValue='$keyValue', agencyAccount='$agencyAccount', " +
                "cpfOwnerAccount='$cpfOwnerAccount', accountInstitution='$accountInstitution', nameOwnerAccount='$nameOwnerAccount', " +
                "accountNumber='$accountNumber', typeAccount=$typeAccount, pixId='$pixId', createdIn=$createdIn)"
    }

    fun toListPixKeyResponse(): ListPixKeyResponse.ListPixKeyDetails? {
        return ListPixKeyResponse.ListPixKeyDetails.newBuilder()
                    .setPixId(this.pixId)
                    .setClientId(this.clientId)
                    .setKeyType(KeyTypeRequest.valueOf(this.type.name))
                    .setKeyValue(this.keyValue)
                    .setType(AccountType.valueOf(this.typeAccount.name))
                    .build()
    }

}

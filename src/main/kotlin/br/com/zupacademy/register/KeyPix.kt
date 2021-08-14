package br.com.zupacademy.register

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
    val pixId: String = UUID.randomUUID().toString()
    @Column(nullable = false)
    val createdIn: LocalDateTime = LocalDateTime.now()
    override fun toString(): String {
        return "KeyPix(clientId='$clientId', KeyType=$type, keyValue='$keyValue', agencyAccount='$agencyAccount', cpfOwnerAccount='$cpfOwnerAccount', accountInstitution='$accountInstitution', nameOwnerAccount='$nameOwnerAccount', accountNumber='$accountNumber', typeAccount=$typeAccount, pixId='$pixId', createdIn=$createdIn)"
    }

    fun belongsClient(clientId: String): Boolean {
        return this.clientId == clientId
    }

}

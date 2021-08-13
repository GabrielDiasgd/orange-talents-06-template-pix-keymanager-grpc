package br.com.zupacademy.integration.responses

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected

@Introspected
data class AccountResponse(
    @field:JsonProperty("tipo")
    val type: String,
    @field:JsonProperty("numero")
    val number: String,
    @field:JsonProperty("agencia")
    val agency: String,
    @field:JsonProperty("instituicao")
    val institution: InstitutionResponse,
    @field:JsonProperty("titular")
    val ownerAccount: OwnerAccount

)

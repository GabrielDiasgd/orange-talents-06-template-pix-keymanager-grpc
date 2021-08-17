package br.com.zupacademy.integration.itau.responses

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected

@Introspected
data class OwnerAccount(
    val id: String,
    @field:JsonProperty("nome")
    val name: String,
    val cpf: String
)
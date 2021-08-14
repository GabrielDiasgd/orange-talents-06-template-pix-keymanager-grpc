package br.com.zupacademy.delete

import javax.validation.constraints.NotBlank

data class DeleteKeyRequest(
    @field:NotBlank
    val pixId: String,
    @field:NotBlank
    val clientId: String
)
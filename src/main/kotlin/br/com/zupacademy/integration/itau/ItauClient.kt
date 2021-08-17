package br.com.zupacademy.integration.itau

import br.com.zupacademy.integration.itau.responses.AccountResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("\${client.http.itau}")
interface ItauClient {

    @Get("/{clientId}/contas{?tipo}")
    fun findClient (@PathVariable clientId: String, @QueryValue tipo: String) : HttpResponse<AccountResponse>
}
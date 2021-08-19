package br.com.zupacademy.integration.bcb

import br.com.zupacademy.integration.bcb.register.BCBRegisterKeyRequest
import br.com.zupacademy.integration.bcb.register.BcbRegisterKeyResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client("\${client.http.bcb}")
interface BcbClient {

    @Get(value = "/{key}", consumes = [MediaType.APPLICATION_XML] )
    fun findByKeyBcb(@PathVariable key: String):HttpResponse<PixKeyDetailsResponse>

    @Post(produces = [MediaType.APPLICATION_XML], consumes = [MediaType.APPLICATION_XML])
    fun registerKeyBcb(@Body bcbRegisterKeyRequest: BCBRegisterKeyRequest): HttpResponse<BcbRegisterKeyResponse>

    @Delete(value = "/{key}" ,produces = [MediaType.APPLICATION_XML], consumes = [MediaType.APPLICATION_XML])
    fun deleteKeyBcb(@PathVariable key: String, @Body bcbDeleteKeyRequest: BcbDeleteKeyRequest): HttpResponse<BcbDeleteKeyResponse>
}
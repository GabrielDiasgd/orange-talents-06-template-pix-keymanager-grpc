package br.com.zupacademy.find

import br.com.zupacademy.integration.bcb.BcbClient
import br.com.zupacademy.register.KeyPixRepository
import br.com.zupacademy.shared.exceptions.PixKeyNotFoundException
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import org.slf4j.LoggerFactory
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
@Introspected
sealed class Filter {

    abstract fun filtrate(repository: KeyPixRepository, bcbClient: BcbClient): PixKeyDetails

@Introspected
data class ByPixId(
    @field:NotBlank val pixId: String,
    @field:NotBlank val clientId: String
): Filter() {

    override fun filtrate(repository: KeyPixRepository, bcbClient: BcbClient): PixKeyDetails {
        return repository.findById(pixId)
            .filter { it.belongsClient(clientId) }
            .map(PixKeyDetails::of)
            .orElseThrow { PixKeyNotFoundException("Chave pix não encontrada ou não pertence ao cliente") }
    }
}

@Introspected
data class ByKey(
    @field:NotBlank @Size(max = 77) val key: String
) : Filter() {

    val LOGGER = LoggerFactory.getLogger(this::class.java)


    override fun filtrate(repository: KeyPixRepository, bcbClient: BcbClient): PixKeyDetails {

        println("Chave $key")
        return repository.findByKeyValue(key)
            .map(PixKeyDetails::of)
            .orElseGet {
                LOGGER.info("Consultando chave $key no Banco Central")

                val response = bcbClient.findByKeyBcb(key)
                when(response.status) {
                    HttpStatus.OK -> response.body()?.toPixKeyDetails()
                    else -> throw PixKeyNotFoundException("Chave Pix não encontrada no banco central")
                }
            }
    }
}

@Introspected
class Invalid() : Filter() {

    override fun filtrate(repository: KeyPixRepository, bcbClient: BcbClient): PixKeyDetails {
        throw IllegalStateException("Chave Pix inválida ou não informada")
    }
}
}

package br.com.zupacademy.register

import br.com.zupacademy.register.KeyPix
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface KeyPixRepository: JpaRepository<KeyPix, String> {

    fun existsByKeyValue(keyValue: String): Boolean
}
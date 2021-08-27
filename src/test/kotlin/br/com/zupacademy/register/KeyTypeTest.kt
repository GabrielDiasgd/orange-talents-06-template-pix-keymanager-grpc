package br.com.zupacademy.register

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class KeyTypeTest {

    @Test
    fun `deve ser valido quando cpf for um numero valido`() {
        with(KeyType.CPF) {
            assertTrue(validator("44719190839"))
        }
    }
    @Test
    internal fun `nao deve ser valido quando cpf for no formato incorreto`() {
        with(KeyType.CPF) {
            assertFalse(validator("4471919083977777"))
        }
    }

    @Test
    internal fun `nao deve ser valido quando cpf for nulo ou vazio`() {
        with(KeyType.CPF) {
            assertFalse(validator(null))
            assertFalse(validator(""))
        }
    }

    //Phone
    @Test
    internal fun `deve ser valido quando phone for em formato correto`() {
        with(KeyType.PHONE) {
            assertTrue(validator("+5585988714077"))
        }
    }

    @Test
    internal fun `nao deve ser valido quando phone for no formato incorreto`() {
        with(KeyType.PHONE) {
            assertFalse(validator("1316531321321"))
        }
    }

    @Test
    internal fun `nao deve ser valido quando phone for nulo ou vazio`() {
        with(KeyType.PHONE) {
            assertFalse(validator(null))
            assertFalse(validator(""))
        }
    }
    //email
    @Test
    internal fun `deve ser valido quando email for com formato correto`() {
        with(KeyType.EMAIL){
            assertTrue(validator("gabriel@gmail.com"))
        }
    }

    @Test
    internal fun `nao deve ser valido quando email for com formato invalido`() {
        with(KeyType.EMAIL) {
            assertFalse(validator("gabriel@"))
        }
    }

    @Test
    internal fun `nao deve ser valido quando email for nulo ou vazio`() {
        with(KeyType.EMAIL) {
            assertFalse(validator(null))
            assertFalse(validator(""))
        }
    }

    //Random
    @Test
    internal fun `deve ser valido quando informado valor nulo vazio`() {
        with(KeyType.RANDOM) {
            assertTrue(validator(null))
            assertTrue(validator(""))
        }
    }

    @Test
    internal fun `nao deve ser valido quando informado algum valor`() {
        with(KeyType.RANDOM) {
            assertFalse(validator("algum valor"))
        }
    }
}
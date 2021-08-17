package br.com.zupacademy.integration.bcb

import br.com.zupacademy.register.KeyPix

data class BcbDeleteKeyRequest(val key: String) {
    val participant: String = KeyPix.ITAU_BANCO_ISPB
}
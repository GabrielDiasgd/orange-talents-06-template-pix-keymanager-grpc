package br.com.zupacademy.integration.bcb

import java.time.LocalDateTime

class BcbDeleteKeyResponse(val key: String, val participant: String, val deletedAt: LocalDateTime)
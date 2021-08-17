package br.com.zupacademy.shared

import br.com.zupacademy.AccountType
import br.com.zupacademy.KeyTypeRequest
import br.com.zupacademy.PixKeyRegistrationRequest
import br.com.zupacademy.register.Account
import br.com.zupacademy.register.KeyType
import br.com.zupacademy.register.NewKeyPix

fun PixKeyRegistrationRequest.toModel(): NewKeyPix {
    return NewKeyPix(
        clientId,
        if(keyType.equals(KeyTypeRequest.UNKNOWN_KEY)) null else KeyType.valueOf(keyType.name),
        keyValue,
        if (account.equals(AccountType.UNKNOWN_ACCOUNT))null else Account.valueOf(account.name)
    )
}

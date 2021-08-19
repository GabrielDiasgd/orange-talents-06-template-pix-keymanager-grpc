package br.com.zupacademy.shared

import br.com.zupacademy.AccountType
import br.com.zupacademy.FindPixKeyRequest
import br.com.zupacademy.KeyTypeRequest
import br.com.zupacademy.PixKeyRegistrationRequest
import br.com.zupacademy.find.Filter
import br.com.zupacademy.register.Account
import br.com.zupacademy.register.KeyType
import br.com.zupacademy.register.NewKeyPix
import javax.validation.ConstraintViolationException
import javax.validation.Validator

fun PixKeyRegistrationRequest.toModel(): NewKeyPix {
    return NewKeyPix(
        clientId,
        if(keyType.equals(KeyTypeRequest.UNKNOWN_KEY)) null else KeyType.valueOf(keyType.name),
        keyValue,
        if (account.equals(AccountType.UNKNOWN_ACCOUNT))null else Account.valueOf(account.name)
    )
}

fun FindPixKeyRequest.toModel(validator: Validator): Filter {

    val filter = when (filterCase) {
        FindPixKeyRequest.FilterCase.PIXID -> pixId.let {
           Filter.ByPixId(it.pixId, it.clientId)}
        FindPixKeyRequest.FilterCase.KEY -> Filter.ByKey(key)
        FindPixKeyRequest.FilterCase.FILTER_NOT_SET -> Filter.Invalid()
    }

    val violations = validator.validate(filter)
    if (violations.isNotEmpty()){
        throw ConstraintViolationException(violations)
    }
    return filter
}
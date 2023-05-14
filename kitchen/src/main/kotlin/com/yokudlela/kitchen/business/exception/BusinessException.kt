package com.yokudlela.kitchen.business.exception

data class BusinessException(
    val errorType: ErrorType,
) : Exception()
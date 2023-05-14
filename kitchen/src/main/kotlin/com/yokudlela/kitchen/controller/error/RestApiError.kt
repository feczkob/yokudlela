package com.yokudlela.kitchen.controller.error

class RestApiError(
    // TODO add correlationId
    val errorCode: String,
    val stackTrace: String?,
) {

    companion object {

        const val ERROR_CODE_UNKNOWN_ERROR = "error.unknown"
        const val ERROR_CODE_MISSING_ENTITY = "error.missing.entity"
        const val ERROR_CODE_INVALID_STATUS_CHANGE = "error.invalid.status.change"
        const val ERROR_CODE_INTERFACE_VALIDATION_ERROR = "error.interface.validation"
    }


}
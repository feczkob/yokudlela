package com.yokudlela.kitchen.controller.error

import com.yokudlela.kitchen.business.exception.ErrorType
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestApiErrorCodeConverter(
    private val businessInterfaceCodes: MutableMap<ErrorType, String> = EnumMap(ErrorType::class.java),
) {

    init {
        businessInterfaceCodes[ErrorType.UNKNOWN_ERROR] = RestApiError.ERROR_CODE_UNKNOWN_ERROR
        businessInterfaceCodes[ErrorType.MISSING_ENTITY] = RestApiError.ERROR_CODE_MISSING_ENTITY
        businessInterfaceCodes[ErrorType.INVALID_STATUS_CHANGE] = RestApiError.ERROR_CODE_INVALID_STATUS_CHANGE
    }

    /**
     * Function that converts errorType to String
     * The returned string will appear as an error message
     * @param errorType ErrorType
     * @return String
     */
    fun getRestApiErrorCode(errorType: ErrorType): String {
        var restErrorCode: String?
        return if (businessInterfaceCodes[errorType]
                .also { restErrorCode = it } == null
        ) RestApiError.ERROR_CODE_UNKNOWN_ERROR else restErrorCode!!
    }

}
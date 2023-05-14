package com.yokudlela.kitchen.controller.error

import com.yokudlela.kitchen.business.exception.BusinessException
import com.yokudlela.kitchen.business.exception.ErrorType
import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.net.BindException

@ControllerAdvice
@Slf4j
class ExceptionHandlerControllerAdvice(
    private val restApiErrorCodeConverter: RestApiErrorCodeConverter,
) {
    // TODO log correlationId
    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(exception: BusinessException): ResponseEntity<RestApiError> {
        logger.error { exception.stackTraceToString() }
        val restApiError = RestApiError(
            restApiErrorCodeConverter.getRestApiErrorCode(exception.errorType),
            exception.stackTraceToString()
        )
        return ResponseEntity.status(mapErrorTypeToStatus(exception.errorType)).body(restApiError)
    }

    // * BindException:
    // + pl.: Rest interface-en custom handlerArgumentResolver es converter nelkuli resolvalaskor dobott Bean validation
    // * MethodArgumentTypeMismatchException:
    // + pl.: Rest interface-en torteno org.springframework.core.convert.converter.Converter#convert exception eseten
    // * MethodArgumentNotValidException:
    // + pl.: Rest interface-en torteno Bean Validation hiba eseten
    @ExceptionHandler(
        BindException::class,
        MethodArgumentTypeMismatchException::class,
        MethodArgumentNotValidException::class
    )
    fun handleValidationException(
        exception: Exception,
    ): ResponseEntity<RestApiError> {
        logger.error { exception.stackTraceToString() }
        val restApiError = RestApiError(
            RestApiError.ERROR_CODE_INTERFACE_VALIDATION_ERROR,
            exception.stackTraceToString()
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restApiError)
    }

    @ExceptionHandler(Exception::class)
    fun handleOthers(exception: java.lang.Exception): ResponseEntity<RestApiError> {
        logger.error { exception.stackTraceToString() }
        val restApiError = RestApiError(RestApiError.ERROR_CODE_UNKNOWN_ERROR, exception.stackTraceToString())
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(restApiError)
    }

    private fun mapErrorTypeToStatus(errorType: ErrorType): HttpStatus {
        val status: HttpStatus = when (errorType) {
            ErrorType.NOT_FINISHED_ALL_ITEMS -> HttpStatus.BAD_REQUEST
            ErrorType.INVALID_STATUS_CHANGE -> HttpStatus.BAD_REQUEST
            ErrorType.MISSING_ENTITY -> HttpStatus.NOT_FOUND
            ErrorType.UNKNOWN_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR
        }
        return status
    }
}
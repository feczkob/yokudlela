package com.yokudlela.kitchen.config

import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.client.ResponseErrorHandler
import java.io.IOException
import javax.ws.rs.BadRequestException
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotFoundException


@Component
class RestTemplateResponseErrorHandler : ResponseErrorHandler {
    // * https://www.baeldung.com/spring-rest-template-error-handling

    @Throws(IOException::class)
    override fun hasError(httpResponse: ClientHttpResponse): Boolean {
        return (((httpResponse.statusCode.series() === HttpStatus.Series.CLIENT_ERROR)
                || (httpResponse.statusCode.series() === HttpStatus.Series.SERVER_ERROR)))
    }

    @Throws(IOException::class)
    override fun handleError(httpResponse: ClientHttpResponse) {
        if (httpResponse.statusCode.series() === HttpStatus.Series.SERVER_ERROR) {
            // * handle SERVER_ERROR
            throw InternalError()
        } else if (httpResponse.statusCode.series() === HttpStatus.Series.CLIENT_ERROR) {
            // * handle CLIENT_ERROR
            if (httpResponse.statusCode === HttpStatus.BAD_REQUEST) {
                throw BadRequestException()
            }
            if (httpResponse.statusCode === HttpStatus.NOT_FOUND) {
                throw NotFoundException()
            }
            if (httpResponse.statusCode === HttpStatus.FORBIDDEN) {
                throw ForbiddenException()
            }
        }
    }
}
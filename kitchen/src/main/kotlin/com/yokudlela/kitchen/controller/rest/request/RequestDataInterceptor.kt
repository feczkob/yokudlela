package com.yokudlela.kitchen.controller.rest.request

import com.yokudlela.kitchen.application.RequestScopedBean
import com.yokudlela.kitchen.controller.Constants.Companion.AUTHORIZATION
import com.yokudlela.kitchen.controller.Constants.Companion.CORRELATION_ID
import com.yokudlela.kitchen.controller.rest.jwt.JwtParser
import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@Slf4j
class RequestDataInterceptor: HandlerInterceptor {

    @Autowired
    private lateinit var accessTokenToRequestScopedBeanConverter: AccessTokenToRequestScopedBeanConverter

    @Autowired
    private lateinit var jwtParser: JwtParser

    @Autowired
    private lateinit var requestScopedBean: RequestScopedBean

    private val logger = KotlinLogging.logger {}

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        requestScopedBean.correlationId = if (request.getHeader(CORRELATION_ID) == null) {
            UUID.randomUUID().toString()
        } else {
            request.getHeader(CORRELATION_ID)
        }
        val authHeader = request.getHeader(AUTHORIZATION)
        if(null != authHeader) {
            accessTokenToRequestScopedBeanConverter.convert(jwtParser.parseJWT(authHeader))
        }
        return true
    }
}
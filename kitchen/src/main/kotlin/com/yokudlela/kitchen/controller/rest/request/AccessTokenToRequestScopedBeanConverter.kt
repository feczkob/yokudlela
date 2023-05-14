package com.yokudlela.kitchen.controller.rest.request

import com.yokudlela.kitchen.application.RequestScopedBean
import com.yokudlela.kitchen.controller.rest.jwt.AccessToken
import org.springframework.stereotype.Component

@Component
class AccessTokenToRequestScopedBeanConverter(
    private val requestScopedBean: RequestScopedBean
) {

    fun convert(accessToken : AccessToken?) {
        if (accessToken != null) {
            requestScopedBean.userName = accessToken.userName
        }
        if (accessToken != null) {
            requestScopedBean.roles = accessToken.roles
        }
    }
}
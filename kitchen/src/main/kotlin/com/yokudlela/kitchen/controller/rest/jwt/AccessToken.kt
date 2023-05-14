package com.yokudlela.kitchen.controller.rest.jwt

import lombok.NoArgsConstructor
import java.util.*

@NoArgsConstructor
data class AccessToken(
    var subject: String = "",
    var sessionId: String = "",
    var expirationDate: Date = Date(),
    var issuedDate: Date = Date(),
    var channelId: String = "",
    var userName: String = "",
    var roles: Set<String> = mutableSetOf()
) {

//    fun setRoles()
}
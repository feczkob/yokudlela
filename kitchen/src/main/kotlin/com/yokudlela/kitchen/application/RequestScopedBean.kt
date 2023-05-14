package com.yokudlela.kitchen.application

import lombok.NoArgsConstructor

@NoArgsConstructor
class RequestScopedBean(
    var correlationId: String = "",
    var userName: String = "",
    var roles: Set<String> = mutableSetOf(),
)
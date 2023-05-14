package com.yokudlela.kitchen.controller.rest.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*

@Component
@Slf4j
class JwtParser {

    private val logger = KotlinLogging.logger {}

    companion object {
        const val JWT_BEARER = "Bearer "
        const val ALLOWED_CLOCK_SKEW_SECONDS = 120
        const val JWT_SECTION_DELIMITER = '.'
        const val CLAIM_USER_NAME = "name"
        const val CLAIM_RESOURCE_ACCESS = "resource_access"
        const val ACCOUNT = "account"
        const val ROLES = "roles"
    }

    fun parseJWT(jwt: String): AccessToken? {
        val claims: Claims? = getClaims(jwt)
        if (null != claims) {
            val accessToken = AccessToken()
            accessToken.subject = claims.subject
            accessToken.sessionId = claims.id
            accessToken.expirationDate = claims.expiration
            accessToken.issuedDate = claims.issuedAt
            accessToken.userName = claims.get(CLAIM_USER_NAME, String::class.java)
            accessToken.roles = getRoles(claims)
            return accessToken
        }
        return null
    }

    private fun getClaims(tokenValue: String): Claims? {
        try {
            // ! method "Jwt.parseClaimsJwt" validates "exp" and "nbf" claims, so we need to set this
            val clockSkewSeconds: Long = ALLOWED_CLOCK_SKEW_SECONDS.toLong()
            var tokenValueWithoutSignature =
                tokenValue.substring(0, tokenValue.lastIndexOf(JWT_SECTION_DELIMITER) + 1)
            if (tokenValueWithoutSignature.startsWith(JWT_BEARER)) {
                tokenValueWithoutSignature =
                    tokenValueWithoutSignature.substring(JWT_BEARER.length)
            }
            val jwt = Jwts.parser()
                .setAllowedClockSkewSeconds(clockSkewSeconds)
                .parseClaimsJwt(tokenValueWithoutSignature)
            return jwt.body
        } catch (e: ExpiredJwtException) {
            logger.warn("Expired jwt: " + e.message)
            return e.claims
        } catch (e: JwtException) {
            logger.warn("Unparsable jwt: " + e.message + ", return null")
        }
        return null
    }

    private fun getRoles(claims: Claims) : Set<String> {
        val account : Map<String, List<String>> = claims.get(CLAIM_RESOURCE_ACCESS, Map::class.java)[ACCOUNT] as Map<String, List<String>>
        val roles = account[ROLES]
        return roles!!.toSet()
    }

    private fun decodeTokenParts(token: String) {
        val parts = token.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (part in parts) {
            val bytes: ByteArray = Base64.getUrlDecoder().decode(part)
            val decodedString = String(bytes, StandardCharsets.UTF_8)
            println("Decoded: $decodedString")
        }
    }
}
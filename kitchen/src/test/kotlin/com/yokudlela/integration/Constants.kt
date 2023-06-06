package com.yokudlela.integration

class Constants {

    companion object {

        const val ROOT_URI = "root-uri"
        const val KEYCLOAK_URI = "keycloak-uri"
        const val HTTP_LOCALHOST = "http://localhost"
        const val KEYCLOAK_REALM = "yokudlela"
        const val KEYCLOAK_TOKEN_URL = "realms/$KEYCLOAK_REALM/protocol/openid-connect/token"
        const val KEYCLOAK_CLIENT = "keycloak-client"
        const val KEYCLOAK_CLIENT_VALUE = "account"
        const val KEYCLOAK_AUTH_SERVER_URL = "keycloak.auth-server-url"
        const val TEST_FILES_PATH = "test-files-path";
        const val TEST_FILES_PATH_VALUE = "src/test/resources/test_objects/"
    }
}
package com.androiderik

import com.androiderik.data.checkIfUserExists
import com.androiderik.data.checkPasswordForEmail
import com.androiderik.data.collections.User
import com.androiderik.data.registerUser
import com.androiderik.routes.loginRoute
import com.androiderik.routes.noteRoutes
import com.androiderik.routes.registerRoute
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    //append extra information
    install(DefaultHeaders)
    //log http requests
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()
    val myRealm = environment.config.property("jwt.realm").getString()
    install(Authentication) {
       // configureAuth()
        configureAuthWithJWT(
            secret,
            audience,
            issuer,
            myRealm
        )
    }
    //for define url endpoints
    install(Routing) {
        registerRoute()
        loginRoute(
            audience,
            issuer,
            secret
        )
        noteRoutes()
    }
}

private fun Authentication.Configuration.configureAuth() {
    basic {
        realm = "Note Server"
        validate { cridentials ->
            val email = cridentials.name
            val password = cridentials.password
            if(checkPasswordForEmail(email, password)) {
                UserIdPrincipal(email)
            } else null
        }
    }
}

private fun Authentication.Configuration.configureAuthWithJWT(
    secret: String,
    audience: String,
    issuer: String,
    myRealm: String
) {
    jwt("auth-jwt") {
        realm = myRealm
        verifier(
            JWT
            .require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build())
        validate { credential ->
            if (credential.payload.getClaim("username").asString() != "") {
                JWTPrincipal(credential.payload)
            } else {
                null
            }
        }
    }
}


package com.androiderik.routes

import com.androiderik.data.checkPasswordForEmail
import com.androiderik.data.requests.AccountRequest
import com.androiderik.data.responses.SimpleResponse
import com.androiderik.data.responses.TokenResponse
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*

fun Route.loginRoute(
    audience: String,
    issuer: String,
    secret: String
) {
    route("/login") {
        post {
            val request = try {
                call.receive<AccountRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(BadRequest)
                return@post
            }
            val isPasswordCorrect = checkPasswordForEmail(request.email, request.password)
            if(isPasswordCorrect) {
                //call.respond(OK, SimpleResponse(true, "You are know logged in!"))
                val token = getToken(
                    audience,
                    issuer,
                    secret,
                    request
                )
                call.respond(OK, TokenResponse(token, true))
            } else {
                call.respond(OK, TokenResponse("",false, "The E-Mail or password is incorrect"))
            }
        }
    }
}

private fun getToken(
    audience: String,
    issuer: String,
    secret: String,
    user: AccountRequest
) = JWT.create()
    .withAudience(audience)
    .withIssuer(issuer)
    .withClaim("username", user.email)
    .withExpiresAt(Date(System.currentTimeMillis() + 24 * 60 * 60000))
    .sign(Algorithm.HMAC256(secret))
package com.androiderik.routes

import com.androiderik.data.checkIfUserExists
import com.androiderik.data.collections.User
import com.androiderik.data.registerUser
import com.androiderik.data.requests.AccountRequest
import com.androiderik.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.registerRoute() {
    route("/register") {
        post {
            val request = try {
                call.receive<AccountRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(BadRequest)
                return@post
            }
            val userExists = checkIfUserExists(request.email)
            if(!userExists) {
                if(registerUser(User(request.email, request.password))) {
                    call.respond(SimpleResponse(true, "Successfully created account!"))
                } else {
                    call.respond(SimpleResponse(false, "An unknown error occurred"))
                }
            } else {
                call.respond(OK, SimpleResponse(false, "A user with E-Mail already exists"))
            }
        }
    }
}
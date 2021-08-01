package com.androiderik.routes

import com.androiderik.data.*
import com.androiderik.data.collections.Note
import com.androiderik.data.requests.AddOwnerRequest
import com.androiderik.data.requests.DeleteNoteRequest
import com.androiderik.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.css.tr

fun Route.noteRoutes() {
    route("/getNotes") {
        authenticate("auth-jwt") {
            get {
                //val email = call.principal<UserIdPrincipal>()!!.name
                val email = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString()
                val notes = getNotesForUser(email)
                call.respond(OK, notes)
            }
        }
    }

    route("/addNote") {
        authenticate("auth-jwt") {
            post {
                val note = try {
                    call.receive<Note>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }
                if (saveNote(note)) {
                    call.respond(OK)
                } else {
                    call.respond(Conflict)
                }
            }
        }
    }

    route("/deleteNote") {
        authenticate("auth-jwt") {
            post {
                //val email = call.principal<UserIdPrincipal>()!!.name
                val email = call.principal<JWTPrincipal>()!!.payload.getClaim("username").asString()
                val request = try {
                    call.receive<DeleteNoteRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }
                if (deleteNoteForUser(email, request.id)) {
                    call.respond(OK)
                } else {
                    call.respond(Conflict)
                }
            }
        }
    }

    route("/addOwnerToNote") {
        authenticate("auth-jwt") {
            post {
                val request = try {
                    call.receive<AddOwnerRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }
                if(!checkIfUserExists(request.owner)) {
                    call.respond(
                        OK,
                        SimpleResponse(false, "No user with this E-Mail exists")
                    )
                    return@post
                }
                if(isOwnerOfNote(request.noteId, request.owner)) {
                    call.respond(
                        OK,
                        SimpleResponse(false, "This user is already owner of this note")
                    )
                    return@post
                }
                if(addOwnerToNote(request.noteId, request.owner)) {
                    call.respond(
                        OK,
                        SimpleResponse(true, "${request.owner} can know see this note")
                    )
                } else {
                    call.respond(Conflict)
                }
            }
        }
    }
}
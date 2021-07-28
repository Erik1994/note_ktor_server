package com.androiderik

import com.androiderik.data.collections.User
import com.androiderik.data.registerUser
import com.androiderik.routes.registerRoute
import io.ktor.application.*
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
    //for define url endpoints
    install(Routing) {
        registerRoute()
    }
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

}


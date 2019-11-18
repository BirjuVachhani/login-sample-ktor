package com.example

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        install(StatusPages) {
            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized, UnauthorizedError())
            }
            exception<AuthorizationException> { cause ->
                call.respond(HttpStatusCode.Forbidden)
            }
        }

        post("/login") {
            val loginRequest: LoginRequest = call.receive()
            if (loginRequest.email == "admin@gmail.com" && loginRequest.password == "12345678") {
                call.respond(LoginResponse(true, UUID.randomUUID().toString()))
            } else {
                throw AuthenticationException("Invalid Credentials")
//                call.respond(status = HttpStatusCode.Unauthorized, message = LoginError("Invalid credentials"))
            }
        }
    }
}

class AuthenticationException(msg: String) : RuntimeException(msg)
class AuthorizationException : RuntimeException()

data class JsonSampleClass(val hello: String)

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val success: Boolean, val token: String)

data class UnauthorizedError(val error: String = "Invalid Credentials")
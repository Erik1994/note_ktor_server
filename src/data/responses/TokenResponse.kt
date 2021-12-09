package com.androiderik.data.responses

data class TokenResponse(
    val token: String,
    val successful: Boolean,
    val message: String = ""
)
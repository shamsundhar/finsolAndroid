package com.finsol.tech.domain.marketdata

import com.google.gson.annotations.SerializedName

data class SessionValidateResponse (
    @SerializedName("message" ) var message : String? = null
)
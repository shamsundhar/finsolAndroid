package com.finsol.tech.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class LoginResponseDomainModel(val status: Boolean, val userID : String) : Parcelable
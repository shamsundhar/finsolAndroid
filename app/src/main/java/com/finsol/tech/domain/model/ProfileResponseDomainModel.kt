package com.finsol.tech.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ProfileResponseDomainModel(val emailid: String, val gender : String, val name : String, val phone : String) : Parcelable
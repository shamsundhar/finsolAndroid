package com.finsol.tech.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ProfileResponseDomainModel(val emailid: String, val firstname:String, val gender : String, val lastname:String, val name : String, val phone : String) : Parcelable
package com.finsol.tech.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Market(val securityID: String, val askPrice: ArrayList<ArrayList<Float>>, val bidPrice: ArrayList<ArrayList<Float>>) :
    Parcelable





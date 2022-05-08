package com.finsol.tech.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Market(val securityID: String, val askPrice: ArrayList<ArrayList<Int>>, val bidPrice: ArrayList<ArrayList<Int>>) :
    Parcelable





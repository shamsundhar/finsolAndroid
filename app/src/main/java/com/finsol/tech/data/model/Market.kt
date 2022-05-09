package com.finsol.tech.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Market(val securityID: String,
                  val askPrice: ArrayList<ArrayList<Float>>,
                  val bidPrice: ArrayList<ArrayList<Float>>,
                  val LTP: String,
                  val OpenPrice: String,
                  val HighPrice: String,
                  val LowPrice: String,
                  val ClosePrice: String,
                  val Volume: String,
                  val OpenInterest: String,
                  val DPRHigh: String,
                  val DPRLow: String,
) : Parcelable





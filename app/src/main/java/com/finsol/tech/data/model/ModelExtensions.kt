package com.finsol.tech.data.model

import com.finsol.tech.domain.model.LoginResponseDomainModel
import com.finsol.tech.domain.model.ProfileResponseDomainModel
import com.finsol.tech.util.Utilities
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray


//fun Market.toMarketDomain() = MarketDomainModel(GetMarketDataResult = GetMarketDataResult)

fun LoginResponse.toLoginDomain() =
    LoginResponseDomainModel(status = status, message = message, userID = userID)

fun ProfileResponse.toProfileDomain() = ProfileResponseDomainModel(
    emailid = emailid,
    firstname = firstname,
    gender = gender,
    lastname = lastname,
    name = name,
    phone = phone
)

fun String.toMarketData(): Market {
    val jsonData = JSONArray(this)
    val securityID: String = jsonData[0] as String
    val askPriceArray: ArrayList<ArrayList<Float>> = Gson().fromJson(
        jsonData[1].toString(),
        object : TypeToken<ArrayList<ArrayList<Float>>>() {}.getType()
    )
    val bidPriceArray: ArrayList<ArrayList<Float>> = Gson().fromJson(
        jsonData[2].toString(),
        object : TypeToken<ArrayList<ArrayList<Float>>>() {}.getType()
    )
    val LTP: String = jsonData[3].toString()
    val OpenPrice: String = jsonData[4].toString()
    val HighPrice: String = jsonData[5].toString()
    val LowPrice: String = jsonData[6].toString()
    val ClosePrice: String = jsonData[7].toString()
    val Volume: String = jsonData[8].toString()
    val OpenInterest: String = jsonData[9].toString()
    val DPRHigh: String = jsonData[10].toString()
    val DPRLow: String = jsonData[11].toString()

    return Market(
        securityID,
        askPriceArray,
        bidPriceArray,
        LTP,
        OpenPrice,
        HighPrice,
        LowPrice,
        ClosePrice,
        Volume, OpenInterest, DPRHigh, DPRLow
    )

}

fun GetAllContractsResponse.maskResponse(): GetAllContractsResponse {
    val time = Utilities.getCurrentTime()
    this.watchlist1.map {
        it.updatedTime = time
    }
    this.watchlist2.map {
        it.updatedTime = time
    }
    this.watchlist3.map {
        it.updatedTime = time
    }
    return this
}


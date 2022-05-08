package com.finsol.tech.data.model

import com.finsol.tech.domain.model.LoginResponseDomainModel
import com.finsol.tech.domain.model.ProfileResponseDomainModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray


//fun Market.toMarketDomain() = MarketDomainModel(GetMarketDataResult = GetMarketDataResult)

fun LoginResponse.toLoginDomain() = LoginResponseDomainModel(status = status,message = message, userID = userID)

fun ProfileResponse.toProfileDomain() = ProfileResponseDomainModel(emailid = emailid, firstname = firstname, gender = gender, lastname= lastname, name = name, phone = phone)

fun String.toMarketData(): Market {
    val jsonData = JSONArray(this)
    val securityID : String = jsonData[0] as String
    val askPriceArray: ArrayList<ArrayList<Float>> = Gson().fromJson(
        jsonData[1].toString(),
        object : TypeToken<ArrayList<ArrayList<Float>>>() {}.getType()
    )
    val bidPriceArray: ArrayList<ArrayList<Float>> = Gson().fromJson(
        jsonData[1].toString(),
        object : TypeToken<ArrayList<ArrayList<Float>>>() {}.getType()
    )
    return Market(securityID,askPriceArray, bidPriceArray)

}

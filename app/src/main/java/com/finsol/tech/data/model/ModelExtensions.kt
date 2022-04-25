package com.finsol.tech.data.model

import com.finsol.tech.domain.model.LoginResponseDomainModel
import com.finsol.tech.domain.model.MarketDomainModel


fun Market.toMarketDomain() = MarketDomainModel(accountIDVStatus = accountIDVStatus)

fun LoginResponse.toLoginDomain(): LoginResponseDomainModel {
    var data: String = UserLoginResult
    data = data.replace("[", "").replace("]", "")

    val status: Boolean = data.split(",")[0].equals("true", true)
    val userID: String = data.split(",")[1]

    return LoginResponseDomainModel(status = status, userID = userID)
}


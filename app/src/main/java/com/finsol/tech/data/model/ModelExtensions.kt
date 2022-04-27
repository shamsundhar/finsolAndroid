package com.finsol.tech.data.model

import com.finsol.tech.domain.model.LoginResponseDomainModel
import com.finsol.tech.domain.model.MarketDomainModel
import com.google.gson.Gson


fun Market.toMarketDomain() = MarketDomainModel(accountIDVStatus = accountIDVStatus)

fun LoginResponse.toLoginDomain() = LoginResponseDomainModel(status = status,message = message, userID = userID)



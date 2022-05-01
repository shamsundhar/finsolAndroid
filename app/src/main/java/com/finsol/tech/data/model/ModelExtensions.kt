package com.finsol.tech.data.model

import com.finsol.tech.domain.model.LoginResponseDomainModel
import com.finsol.tech.domain.model.MarketDomainModel
import com.finsol.tech.domain.model.PortfolioResponseDomainModel
import com.finsol.tech.domain.model.ProfileResponseDomainModel
import com.google.gson.Gson


fun Market.toMarketDomain() = MarketDomainModel(accountIDVStatus = accountIDVStatus)

fun LoginResponse.toLoginDomain() = LoginResponseDomainModel(status = status,message = message, userID = userID)

fun ProfileResponse.toProfileDomain() = ProfileResponseDomainModel(emailid = emailid,gender = gender, name = name, phone = phone)

fun PortfolioResponse.toPortfolioDomain() = PortfolioResponseDomainModel(GetPortfolioResult = GetPortfolioResult)



package com.finsol.tech.domain

import com.finsol.tech.data.model.*
import kotlinx.coroutines.flow.Flow

interface LoginResponseDataRepository {
    suspend fun getLoginData(userID: String, password: String): Flow<ResponseWrapper<LoginResponse>>
    suspend fun getProfileData(userID: String): Flow<ResponseWrapper<ProfileResponse>>
    suspend fun getAllContractsData(userID: String): Flow<ResponseWrapper<GetAllContractsResponse>>
    suspend fun getPendingOrdersData(userID: String): Flow<ResponseWrapper<Array<PendingOrderModel>>>
    suspend fun getPortfolioResponse(userID: String): Flow<ResponseWrapper<PortfolioResponse>>
}
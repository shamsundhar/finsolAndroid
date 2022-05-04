package com.finsol.tech.domain

import com.finsol.tech.data.model.*
import kotlinx.coroutines.flow.Flow

interface LoginResponseDataRepository {
    suspend fun getLoginData(userID: String, password: String): Flow<ResponseWrapper<LoginResponse>>
    suspend fun getProfileData(userID: String): Flow<ResponseWrapper<ProfileResponse>>
    suspend fun getAllContractsData(userID: String): Flow<ResponseWrapper<GetAllContractsResponse>>
    suspend fun getPendingOrdersData(userID: String): Flow<ResponseWrapper<Array<PendingOrderModel>>>
    suspend fun getPortfolioResponse(userID: String): Flow<ResponseWrapper<PortfolioResponse>>
    suspend fun getOrderHistoryData(userID: String): Flow<ResponseWrapper<Array<OrderHistoryModel>>>
    suspend fun addToWatchList(userID: String, watchListNumber:String, securityID:String): Flow<ResponseWrapper<GenericMessageResponse>>
    suspend fun removeFromWatchList(userID: String,watchListNumber:String, securityID:String): Flow<ResponseWrapper<GenericMessageResponse>>
    suspend fun changePassword(userID: String,userName:String, newPassword:String): Flow<ResponseWrapper<GenericMessageResponse>>
    suspend fun register(name: String,email:String, phone:String): Flow<ResponseWrapper<GenericMessageResponse>>
    suspend fun getMarketData(): Flow<ResponseWrapper<Market>>
}
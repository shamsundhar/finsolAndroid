package com.finsol.tech.domain

import com.finsol.tech.data.model.*
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Query

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
    suspend fun forgotPassword(userName: String): Flow<ResponseWrapper<GenericMessageResponse>>
    suspend fun addFunds(userName: String): Flow<ResponseWrapper<GenericMessageResponse>>
    suspend fun withdrawFunds(userName: String): Flow<ResponseWrapper<GenericMessageResponse>>
    suspend fun getExchangeNames(): Flow<ResponseWrapper<Array<ExchangeEnumModel>>>
    suspend fun getExchangeOptions(): Flow<ResponseWrapper<Array<ExchangeOptionsModel>>>
    suspend fun getMarketData(securityID: String, exchangeName: String): Flow<ResponseWrapper<String>>
    suspend fun placeBuyOrder(securityID: String, userID: String, orderType: String, timeInForce: String, price: String, quantity: String):Flow<ResponseWrapper<Boolean>>
    suspend fun placeSellOrder(securityID: String, userID: String, orderType: String, timeInForce: String, price: String, quantity: String):Flow<ResponseWrapper<Boolean>>
    suspend fun modifyOrder(uniqueOrderID: String, stopPrice: String, price: String, quantity: String):Flow<ResponseWrapper<Boolean>>
    suspend fun cancelOrder(uniqueOrderID:String):Flow<ResponseWrapper<Boolean>>
    suspend fun logout(userID: String): Flow<ResponseWrapper<Boolean>>
}
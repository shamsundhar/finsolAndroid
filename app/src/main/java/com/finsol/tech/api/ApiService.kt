package com.finsol.tech.api

import com.finsol.tech.data.model.*
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("QT_Mobile_Host/UserLogin")
    suspend fun getLoginResponse(
        @Query("userName") userName: String,
        @Query("password") password: String
    ): LoginResponse

    @GET("QT_Mobile_Host/GetUserProfile")
    suspend fun getProfileResponse(@Query("userID") userName: String): ProfileResponse

    @GET("QT_Mobile_Host/GetAllContracts")
    suspend fun getAllContractsResponse(@Query("userID") userName: String): GetAllContractsResponse

    @GET("QT_Mobile_Host/GetPendingOrders")
    suspend fun getPendingOrdersResponse(@Query("userID") userName: String): Array<PendingOrderModel>

    @GET("QT_Mobile_Host/GetPortfolio")
    suspend fun getPortfolioResponse(@Query("userID") userID: String) : PortfolioResponse

}
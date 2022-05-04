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

    @GET("QT_Mobile_Host/GetOrdersHistory")
    suspend fun getOrderHistoryResponse(@Query("userID") userName: String): Array<OrderHistoryModel>

    @GET("QT_Mobile_Host/GetPortfolio")
    suspend fun getPortfolioResponse(@Query("userID") userID: String) : PortfolioResponse

    @GET("QT_Mobile_Host/AddToWatchlist")
    suspend fun addToWatchlist(
        @Query("userID") userID: String,
        @Query("watchlistNumber") watchlistNumber: String,
        @Query("securityID") securityID: String
    ): GenericMessageResponse

    @GET("QT_Mobile_Host/RemoveFromWatchlist")
    suspend fun removeFromWatchlist(
        @Query("userID") userID: String,
        @Query("watchlistNumber") watchlistNumber: String,
        @Query("securityID") securityID: String
    ): GenericMessageResponse

    @GET("QT_Mobile_Host/ChangePassword")
    suspend fun changePassword(
        @Query("userid") userID: String,
        @Query("username") userName: String,
        @Query("newpassword") newPassword: String
    ): GenericMessageResponse

    @GET("QT_Mobile_Host/Register")
    suspend fun register(
        @Query("name") name: String,
        @Query("email") email: String,
        @Query("phone") phone: String
    ): GenericMessageResponse

    @GET("QT_Mobile_Host/ForgotPassword")
    suspend fun forgotPassword(
        @Query("username") userName: String
    ): GenericMessageResponse

    @GET("QT_Mobile_Host/AddFunds")
    suspend fun addFunds(
        @Query("username") userName: String
    ): GenericMessageResponse

    @GET("QT_Mobile_Host/WithdrawFunds")
    suspend fun withdrawFunds(
        @Query("username") userName: String
    ): GenericMessageResponse

    @GET("/GetMarketData")
    suspend fun getMarketData(@Query("securityID") securityID: String,
                              @Query("exchangeName") exchangeName: String) : Market

}
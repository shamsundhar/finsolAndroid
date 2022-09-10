package com.finsol.tech.api

import com.finsol.tech.data.model.*
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("QT_Mobile_Host/UserLogin")
    suspend fun getLoginResponse(
        @Query("userName") userName: String,
        @Query(value = "password", encoded = true) password: String
    ): LoginResponse

    @GET("QT_Mobile_Host/GetUserProfile")
    suspend fun getProfileResponse(@Query("userID") userName: String): ProfileResponse

    @GET("QT_Mobile_Host/GetAllContracts")
    suspend fun getAllContractsResponse(@Query("userID") userName: String): GetAllContractsResponse

    @GET("QT_Mobile_Host/GetPendingOrders")
    suspend fun getPendingOrdersResponse(@Query("userID") userName: String): Array<PendingOrderModel>

    @GET("QT_Mobile_Host/GetOrdersHistory")
    suspend fun getOrderHistoryResponse(@Query("userID") userName: String): Array<OrderHistoryModel>

    @GET("QT_Mobile_Host/GetCanceledRejectedOrders")
    suspend fun getOrderBookResponse(@Query("userID") userName: String): Array<RejectedCancelledOrdersResponse>

    @GET("QT_Mobile_Host/GetPortfolio")
    suspend fun getPortfolioResponse(@Query("userID") userID: String): PortfolioResponse

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

    @GET("QT_Mobile_Host/GetMarketData")
    suspend fun getMarketData(
        @Query("securityID") securityID: String,
        @Query("exchangeName") exchangeName: String
    ): String

    @GET("QT_Mobile_Host/GetExchangeEnum")
    suspend fun getExchangeNames(): Array<ExchangeEnumModel>

    @GET("QT_Mobile_Host/GetExchangeOptions")
    suspend fun getExchangeOptions(): Array<ExchangeOptionsModel>

    //    http://43.204.110.131:8001/QT_Mobile_Host/SendBuyOrder?userID=7&securityID=20102&orderType=Limit&timeInForce=1&price=128.91&quantity=10
    @GET("QT_Mobile_Host/SendBuyOrder")
    suspend fun buyOrder(
        @Query("securityID") securityID: String,
        @Query("userID") userID: String,
        @Query("orderType") orderType: String,
        @Query("timeInForce") timeInForce: String,
        @Query("price") price: String,
        @Query("quantity") quantity: String
    ): Boolean

    //    http://35.179.51.36:8001/QT_Mobile_Host/SendSellOrder?userID=7&securityID=20102&orderType=Limit&timeInForce=1&price=128.91&quantity=10
    @GET("QT_Mobile_Host/SendSellOrder")
    suspend fun sellOrder(
        @Query("securityID") securityID: String,
        @Query("userID") userID: String,
        @Query("orderType") orderType: String,
        @Query("timeInForce") timeInForce: String,
        @Query("price") price: String,
        @Query("quantity") quantity: String
    ): Boolean

    //    http://35.179.51.36:8001/QT_Mobile_Host/ModifyOrder?uniqueOrderID=MobileApp1&price=127.9&stopPrice=0&quantity=1
    @GET("QT_Mobile_Host/ModifyOrder")
    suspend fun modifyOrder(
        @Query("uniqueOrderID") uniqueOrderID: String,
        @Query("stopPrice") stopPrice: String,
        @Query("price") price: String,
        @Query("quantity") quantity: String
    ): Int

    //    http://35.179.51.36:8001/QT_Mobile_Host/CancelOrder?uniqueOrderID=MobileApp4
    @GET("QT_Mobile_Host/CancelOrder")
    suspend fun cancelOrder(@Query("uniqueOrderID") uniqueOrderID: String): Boolean

    //    http://43.204.110.131:8001/QT_Mobile_Host/UserLogout?userID=1120
    @GET("QT_Mobile_Host/UserLogout")
    suspend fun logout(@Query("userID") userID: String): Boolean

}
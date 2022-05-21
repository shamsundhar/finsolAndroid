package com.finsol.tech.data.remote

import com.finsol.tech.api.ApiService
import com.finsol.tech.api.NoConnectivityException
import com.finsol.tech.data.model.*

import com.jukti.clearscoredemo.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

import javax.inject.Inject

class LoginDataSource @Inject constructor(
    private val apiService: ApiService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getLoginData(userID: String, password: String): ResponseWrapper<LoginResponse> {
        return safeApiCall(apiCall = { apiService.getLoginResponse(userID, password) })
    }

    suspend fun getProfileData(userID: String): ResponseWrapper<ProfileResponse> {
        return safeApiCall(apiCall = { apiService.getProfileResponse(userID) })
    }

    suspend fun getAllContractsData(userID: String): ResponseWrapper<GetAllContractsResponse> {
        return safeApiCall(apiCall = { apiService.getAllContractsResponse(userID) })
    }

    suspend fun getPendingOrdersData(userID: String): ResponseWrapper<Array<PendingOrderModel>> {
        return safeApiCall(apiCall = { apiService.getPendingOrdersResponse(userID) })
    }

    suspend fun getOrderHistoryData(userID: String): ResponseWrapper<Array<OrderHistoryModel>> {
        return safeApiCall(apiCall = { apiService.getOrderHistoryResponse(userID) })
    }

    suspend fun getPortfolioResponse(userID: String): ResponseWrapper<PortfolioResponse> {
        return safeApiCall(apiCall = { apiService.getPortfolioResponse(userID) })
    }

    suspend fun addToWatchList(
        userID: String,
        watchListNumber: String,
        securityID: String
    ): ResponseWrapper<GenericMessageResponse> {
        return safeApiCall(apiCall = {
            apiService.addToWatchlist(
                userID,
                watchListNumber,
                securityID
            )
        })
    }

    suspend fun removeFromWatchList(
        userID: String,
        watchListNumber: String,
        securityID: String
    ): ResponseWrapper<GenericMessageResponse> {
        return safeApiCall(apiCall = {
            apiService.removeFromWatchlist(
                userID,
                watchListNumber,
                securityID
            )
        })
    }

    suspend fun changePassword(
        userID: String,
        userName: String,
        newPassword: String
    ): ResponseWrapper<GenericMessageResponse> {
        return safeApiCall(apiCall = { apiService.changePassword(userID, userName, newPassword) })
    }

    suspend fun register(
        name: String,
        email: String,
        phone: String
    ): ResponseWrapper<GenericMessageResponse> {
        return safeApiCall(apiCall = { apiService.register(name, email, phone) })
    }

    suspend fun forgotPassword(username: String): ResponseWrapper<GenericMessageResponse> {
        return safeApiCall(apiCall = { apiService.forgotPassword(username) })
    }

    suspend fun addFunds(username: String): ResponseWrapper<GenericMessageResponse> {
        return safeApiCall(apiCall = { apiService.addFunds(username) })
    }

    suspend fun withdrawFunds(username: String): ResponseWrapper<GenericMessageResponse> {
        return safeApiCall(apiCall = { apiService.withdrawFunds(username) })
    }

    suspend fun getExchangeEnumData(): ResponseWrapper<Array<ExchangeEnumModel>> {
        return safeApiCall(apiCall = { apiService.getExchangeNames() })
    }

    suspend fun getExchangeOptionsData(): ResponseWrapper<Array<ExchangeOptionsModel>> {
        return safeApiCall(apiCall = { apiService.getExchangeOptions() })
    }

    suspend fun getMarketData(securityID: String, exchangeName: String): ResponseWrapper<String> {
        return safeApiCall(apiCall = { apiService.getMarketData(securityID, exchangeName) })
//        return safeApiCall(apiCall = { apiService.getMarketData("229858","MCX")})
    }

    suspend fun placeBuyOrder(
        securityID: String,
        userID: String,
        orderType: String,
        timeInForce: String,
        price: String,
        quantity: String
    ): ResponseWrapper<Boolean> {
        return safeApiCall(apiCall = { apiService.buyOrder(securityID, userID, orderType, timeInForce, price, quantity) })
    }

    suspend fun placeSellOrder(
        securityID: String,
        userID: String,
        orderType: String,
        timeInForce: String,
        price: String,
        quantity: String
    ): ResponseWrapper<Boolean> {
        return safeApiCall(apiCall = { apiService.sellOrder(securityID, userID, orderType, timeInForce, price, quantity) })
    }

    suspend fun <T> safeApiCall(apiCall: suspend () -> T): ResponseWrapper<T> {
        return withContext(ioDispatcher) {
            try {
                ResponseWrapper.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is NoConnectivityException -> ResponseWrapper.NetworkError
                    is IOException -> ResponseWrapper.NetworkError
                    is HttpException -> {
                        val code = throwable.code()
                        val msg = throwable.message()
                        val errorMsg = if (msg.isNullOrEmpty()) {
                            throwable.response()?.errorBody()?.toString()
                        } else {
                            msg
                        }
                        ResponseWrapper.GenericError(code, errorMsg)
                    }
                    else -> {
                        ResponseWrapper.GenericError(null, null)
                    }
                }
            }
        }
    }


    companion object {
        private const val TAG = "LoginDataSource"
    }


}
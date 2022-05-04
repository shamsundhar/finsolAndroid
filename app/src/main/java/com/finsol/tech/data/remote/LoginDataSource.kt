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

class LoginDataSource @Inject constructor(private val apiService: ApiService,
                                          @IoDispatcher private val ioDispatcher: CoroutineDispatcher){

    suspend fun getLoginData(userID : String, password : String): ResponseWrapper<LoginResponse> {
        return safeApiCall(apiCall = { apiService.getLoginResponse(userID,password)})
    }

    suspend fun getProfileData(userID : String): ResponseWrapper<ProfileResponse> {
        return safeApiCall(apiCall = { apiService.getProfileResponse(userID)})
    }

    suspend fun getAllContractsData(userID : String): ResponseWrapper<GetAllContractsResponse> {
        return safeApiCall(apiCall = { apiService.getAllContractsResponse(userID)})
    }

    suspend fun getPendingOrdersData(userID : String): ResponseWrapper<Array<PendingOrderModel>> {
        return safeApiCall(apiCall = { apiService.getPendingOrdersResponse(userID)})
    }

    suspend fun getOrderHistoryData(userID : String): ResponseWrapper<Array<OrderHistoryModel>> {
        return safeApiCall(apiCall = { apiService.getOrderHistoryResponse(userID)})
    }

    suspend fun getPortfolioResponse(userID : String): ResponseWrapper<PortfolioResponse> {
        return safeApiCall(apiCall = { apiService.getPortfolioResponse(userID)})
    }

    suspend fun addToWatchList(userID : String, watchListNumber:String, securityID:String): ResponseWrapper<GenericMessageResponse> {
        return safeApiCall(apiCall = { apiService.addToWatchlist(userID, watchListNumber, securityID)})
    }

    suspend fun removeFromWatchList(userID : String, watchListNumber:String, securityID:String): ResponseWrapper<GenericMessageResponse> {
        return safeApiCall(apiCall = { apiService.removeFromWatchlist(userID, watchListNumber, securityID)})
    }

    suspend fun changePassword(userID : String, userName:String, newPassword:String): ResponseWrapper<GenericMessageResponse> {
        return safeApiCall(apiCall = { apiService.changePassword(userID, userName, newPassword)})
    }

    suspend fun register(name : String, email:String, phone:String): ResponseWrapper<GenericMessageResponse> {
        return safeApiCall(apiCall = { apiService.register(name, email, phone)})
    }

    suspend fun getMarketData(): ResponseWrapper<Market> {
        return safeApiCall(apiCall = { apiService.getMarketData("13280854308698078477","CME")})
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
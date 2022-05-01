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
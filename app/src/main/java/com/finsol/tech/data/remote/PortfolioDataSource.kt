package com.finsol.tech.data.remote

import com.finsol.tech.api.NoConnectivityException
import com.finsol.tech.api.PortfolioResponseApiService
import com.finsol.tech.data.model.PortfolioResponse
import com.finsol.tech.data.model.ResponseWrapper

import com.jukti.clearscoredemo.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

import javax.inject.Inject

class PortfolioDataSource @Inject constructor(private val apiService: PortfolioResponseApiService,
                                              @IoDispatcher private val ioDispatcher: CoroutineDispatcher){

    suspend fun getPortfolioResponse(userID : Int): ResponseWrapper<PortfolioResponse> {
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
        private const val TAG = "PortfolioDataSource"
    }


}
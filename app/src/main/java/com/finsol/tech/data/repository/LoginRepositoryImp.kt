package com.finsol.tech.data.repository

import android.util.Log
import com.finsol.tech.data.model.*
import com.finsol.tech.data.remote.LoginDataSource
import com.finsol.tech.domain.LoginResponseDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class LoginRepositoryImp @Inject constructor(private val remoteDataSource: LoginDataSource):LoginResponseDataRepository{

    override suspend fun getLoginData(userID : String, password : String): Flow<ResponseWrapper<LoginResponse>> {
        return flow {
            Log.e(TAG, "I'm working in thread ${Thread.currentThread().name}")
            emit(remoteDataSource.getLoginData(userID,password))
        }
    }

    override suspend fun getProfileData(userID : String): Flow<ResponseWrapper<ProfileResponse>> {
        return flow {
            Log.e(TAG, "I'm working in thread ${Thread.currentThread().name}")
            emit(remoteDataSource.getProfileData(userID))
        }
    }

    override suspend fun getAllContractsData(userID : String): Flow<ResponseWrapper<GetAllContractsResponse>> {
        return flow {
            Log.e(TAG, "I'm working in thread ${Thread.currentThread().name}")
            emit(remoteDataSource.getAllContractsData(userID))
        }
    }

    override suspend fun getPendingOrdersData(userID : String): Flow<ResponseWrapper<Array<PendingOrderModel>>> {
        return flow {
            Log.e(TAG, "I'm working in thread ${Thread.currentThread().name}")
            emit(remoteDataSource.getPendingOrdersData(userID))
        }
    }

    override suspend fun getOrderHistoryData(userID : String): Flow<ResponseWrapper<Array<OrderHistoryModel>>> {
        return flow {
            Log.e(TAG, "I'm working in thread ${Thread.currentThread().name}")
            emit(remoteDataSource.getOrderHistoryData(userID))
        }
    }

    override suspend fun getPortfolioResponse(userID : String): Flow<ResponseWrapper<PortfolioResponse>> {
        return flow {
            Log.e(TAG, "I'm working in thread ${Thread.currentThread().name}")
            emit(remoteDataSource.getPortfolioResponse(userID))
        }
    }

    override suspend fun addToWatchList(
        userID: String,
        watchListNumber: String,
        securityID: String
    ): Flow<ResponseWrapper<GenericMessageResponse>>{
        return flow {
            Log.e(TAG, "I'm working in thread ${Thread.currentThread().name}")
            emit(remoteDataSource.addToWatchList(userID, watchListNumber, securityID))
        }
    }

    override suspend fun removeFromWatchList(
        userID: String,
        watchListNumber: String,
        securityID: String
    ): Flow<ResponseWrapper<GenericMessageResponse>> {
        return flow {
            Log.e(TAG, "I'm working in thread ${Thread.currentThread().name}")
            emit(remoteDataSource.removeFromWatchList(userID, watchListNumber, securityID))
        }
    }

    override suspend fun changePassword(
        userID: String,
        userName: String,
        newPassword: String
    ): Flow<ResponseWrapper<GenericMessageResponse>> {
        return flow {
            Log.e(TAG, "I'm working in thread ${Thread.currentThread().name}")
            emit(remoteDataSource.changePassword(userID, userName, newPassword))
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        phone: String
    ): Flow<ResponseWrapper<GenericMessageResponse>> {
        return flow {
            Log.e(TAG, "I'm working in thread ${Thread.currentThread().name}")
            emit(remoteDataSource.register(name, email, phone))
        }
    }

    override suspend fun forgotPassword(userName: String): Flow<ResponseWrapper<GenericMessageResponse>> {
        return flow {
            Log.e(TAG, "I'm working in thread ${Thread.currentThread().name}")
            emit(remoteDataSource.forgotPassword(userName))
        }
    }

    override suspend fun addFunds(userName: String): Flow<ResponseWrapper<GenericMessageResponse>> {
        return flow {
            Log.e(TAG, "I'm working in thread ${Thread.currentThread().name}")
            emit(remoteDataSource.addFunds(userName))
        }
    }

    override suspend fun withdrawFunds(userName: String): Flow<ResponseWrapper<GenericMessageResponse>> {
        return flow {
            Log.e(TAG, "I'm working in thread ${Thread.currentThread().name}")
            emit(remoteDataSource.withdrawFunds(userName))
        }
    }

    override suspend fun getExchangeNames(): Flow<ResponseWrapper<Array<ExchangeEnumModel>>> {
        return flow {
            Log.e(TAG, "I'm working in thread ${Thread.currentThread().name}")
            emit(remoteDataSource.getExchangeEnumData())
        }
    }

    override suspend fun getExchangeOptions(): Flow<ResponseWrapper<Array<ExchangeOptionsModel>>> {
        return flow {
            Log.e(TAG, "I'm working in thread ${Thread.currentThread().name}")
            emit(remoteDataSource.getExchangeOptionsData())
        }
    }

    override suspend fun getMarketData(securityID: String, exchangeName: String): Flow<ResponseWrapper<String>> {
        return flow {
            Log.e(TAG, "I'm working in thread ${Thread.currentThread().name}")
            emit(remoteDataSource.getMarketData(securityID,exchangeName))
        }
    }



    companion object{
        private const val TAG = "LoginRepositoryImp"
    }

}
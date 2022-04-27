package com.finsol.tech.data.repository

import android.util.Log
import com.finsol.tech.data.model.LoginResponse
import com.finsol.tech.data.model.Market
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.data.remote.LoginDataSource
import com.finsol.tech.data.remote.MarketDataSource
import com.finsol.tech.domain.LoginResponseDataRepository
import com.finsol.tech.domain.MarketDataRepository
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


    companion object{
        private const val TAG = "LoginRepositoryImp"
    }

}
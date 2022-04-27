package com.finsol.tech.data.repository

import android.util.Log
import com.finsol.tech.data.model.*
import com.finsol.tech.data.remote.AllContractsDataSource
import com.finsol.tech.data.remote.LoginDataSource
import com.finsol.tech.data.remote.MarketDataSource
import com.finsol.tech.data.remote.ProfileDataSource
import com.finsol.tech.domain.AllContractsResponseDataRepository
import com.finsol.tech.domain.LoginResponseDataRepository
import com.finsol.tech.domain.MarketDataRepository
import com.finsol.tech.domain.ProfileResponseDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class AllContractsRepositoryImp @Inject constructor(private val remoteDataSource: AllContractsDataSource): AllContractsResponseDataRepository {

    override suspend fun getAllContractsData(userID : String): Flow<ResponseWrapper<GetAllContractsResponse>> {
        return flow {
            Log.e(TAG, "I'm working in thread ${Thread.currentThread().name}")
            emit(remoteDataSource.getAllContractsData(userID))
        }
    }


    companion object{
        private const val TAG = "AllContractsRepositoryImp"
    }

}
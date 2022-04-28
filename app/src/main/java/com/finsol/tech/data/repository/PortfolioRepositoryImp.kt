package com.finsol.tech.data.repository

import android.util.Log
import com.finsol.tech.data.model.PortfolioResponse
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.data.remote.PortfolioDataSource
import com.finsol.tech.domain.PortfolioResponseDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class PortfolioRepositoryImp @Inject constructor(private val remoteDataSource: PortfolioDataSource):PortfolioResponseDataRepository{

    override suspend fun getPortfolioData(userID : Int): Flow<ResponseWrapper<PortfolioResponse>> {
        return flow {
            Log.e(TAG, "I'm working in thread ${Thread.currentThread().name}")
            emit(remoteDataSource.getPortfolioResponse(userID))
        }
    }


    companion object{
        private const val TAG = "PortfolioRepositoryImp"
    }

}
package com.finsol.tech.data.repository

import android.util.Log
import com.finsol.tech.data.model.Market
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.data.remote.MarketDataSource
import com.finsol.tech.domain.MarketDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class MarketDataRepositoryImp @Inject constructor(private val remoteDataSource: MarketDataSource):MarketDataRepository{

    override suspend fun getMarketData(): Flow<ResponseWrapper<Market>> {
        return flow {
            Log.e(TAG, "I'm working in thread ${Thread.currentThread().name}")
            emit(remoteDataSource.getMarketData())
        }
    }


    companion object{
        private const val TAG = "MarketDataRepositoryImp"
    }

}
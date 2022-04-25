package com.finsol.tech.domain

import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.data.model.Market
import kotlinx.coroutines.flow.Flow

interface MarketDataRepository {
    suspend fun getMarketData(): Flow<ResponseWrapper<Market>>
}
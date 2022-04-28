package com.finsol.tech.domain

import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.data.model.PortfolioResponse
import kotlinx.coroutines.flow.Flow

interface PortfolioResponseDataRepository {
    suspend fun getPortfolioData(userID: Int): Flow<ResponseWrapper<PortfolioResponse>>
}
package com.finsol.tech.domain.marketdata

import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.data.model.toMarketDomain
import com.finsol.tech.domain.MarketDataRepository
import com.finsol.tech.domain.model.MarketDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMarketData @Inject constructor(private val repository : MarketDataRepository) {
    suspend fun execute(): Flow<ResponseWrapper<MarketDomainModel>> {
        return repository.getMarketData().map {
            when(it){
                is ResponseWrapper.Success -> {
                    ResponseWrapper.Success(it.value.toMarketDomain())
                }
                is ResponseWrapper.GenericError -> {
                    ResponseWrapper.GenericError(it.code,it.error)
                }
                is ResponseWrapper.NetworkError -> {
                    ResponseWrapper.NetworkError
                }
            }
        }
    }
}
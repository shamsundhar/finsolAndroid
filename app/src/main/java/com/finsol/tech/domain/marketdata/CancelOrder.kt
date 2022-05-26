package com.finsol.tech.domain.marketdata

import com.finsol.tech.data.model.Market
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.data.model.toMarketData
import com.finsol.tech.domain.LoginResponseDataRepository
import com.finsol.tech.domain.model.MarketDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CancelOrder @Inject constructor(private val repository : LoginResponseDataRepository) {
    suspend fun execute(uniqueOrderID: String): Flow<ResponseWrapper<Boolean>> {
        return repository.cancelOrder(uniqueOrderID).map {
            when(it){
                is ResponseWrapper.Success -> {
                    ResponseWrapper.Success(it.value)
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
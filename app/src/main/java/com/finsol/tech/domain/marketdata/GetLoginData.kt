package com.finsol.tech.domain.marketdata

import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.data.model.toLoginDomain
import com.finsol.tech.data.model.toMarketDomain
import com.finsol.tech.domain.LoginResponseDataRepository
import com.finsol.tech.domain.MarketDataRepository
import com.finsol.tech.domain.model.LoginResponseDomainModel
import com.finsol.tech.domain.model.MarketDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLoginData @Inject constructor(private val repository : LoginResponseDataRepository) {
    suspend fun execute(userID: String, password: String): Flow<ResponseWrapper<LoginResponseDomainModel>> {
        return repository.getLoginData(userID, password).map {
            when(it){
                is ResponseWrapper.Success -> {
                    ResponseWrapper.Success(it.value.toLoginDomain())
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
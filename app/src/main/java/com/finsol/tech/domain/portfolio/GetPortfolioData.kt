package com.finsol.tech.domain.portfolio


import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.data.model.toPortfolioDomain

import com.finsol.tech.domain.PortfolioResponseDataRepository

import com.finsol.tech.domain.model.PortfolioResponseDomainModel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPortfolioData @Inject constructor(private val repository : PortfolioResponseDataRepository) {
    suspend fun execute(userID: Int): Flow<ResponseWrapper<PortfolioResponseDomainModel>> {
        return repository.getPortfolioData(userID).map {
            when(it){
                is ResponseWrapper.Success -> {
                    ResponseWrapper.Success(it.value.toPortfolioDomain())
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
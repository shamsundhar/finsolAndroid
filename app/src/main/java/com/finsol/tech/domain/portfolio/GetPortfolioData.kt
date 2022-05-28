package com.finsol.tech.domain.portfolio

import com.finsol.tech.data.model.*
import com.finsol.tech.domain.LoginResponseDataRepository
import com.finsol.tech.domain.model.ProfileResponseDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPortfolioData @Inject constructor(private val repository : LoginResponseDataRepository) {
    suspend fun execute(userID: String): Flow<ResponseWrapper<PortfolioResponse>> {
        return repository.getPortfolioResponse(userID).map {
            when(it){
                is ResponseWrapper.Success -> {
                    ResponseWrapper.Success(it.value)
                }
                is ResponseWrapper.Success2 -> {
                    ResponseWrapper.Success2
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
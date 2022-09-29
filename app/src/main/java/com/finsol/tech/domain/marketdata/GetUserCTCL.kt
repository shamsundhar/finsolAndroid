package com.finsol.tech.domain.marketdata

import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.data.model.toLoginDomain
import com.finsol.tech.domain.LoginResponseDataRepository
import com.finsol.tech.domain.model.LoginResponseDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetUserCTCL @Inject constructor(private val repository : LoginResponseDataRepository) {
    suspend fun execute(userID: String): Flow<ResponseWrapper<Array<String>>> {
        return repository.getUserCTCL(userID).map {
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
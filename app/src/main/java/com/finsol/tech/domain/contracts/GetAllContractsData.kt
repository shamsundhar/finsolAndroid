package com.finsol.tech.domain.contracts

import com.finsol.tech.data.model.GetAllContractsResponse
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.data.model.maskResponse
import com.finsol.tech.domain.LoginResponseDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllContractsData @Inject constructor(private val repository : LoginResponseDataRepository) {
    suspend fun execute(userID: String): Flow<ResponseWrapper<GetAllContractsResponse>> {
        return repository.getAllContractsData(userID).map {
            when(it){
                is ResponseWrapper.Success -> {
                    ResponseWrapper.Success(it.value.maskResponse())
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
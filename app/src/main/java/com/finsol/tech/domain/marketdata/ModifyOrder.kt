package com.finsol.tech.domain.marketdata

import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.domain.LoginResponseDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ModifyOrder @Inject constructor(private val repository : LoginResponseDataRepository) {
    suspend fun execute(uniqueOrderID: String,
                        stopPrice: String?,
                        price: String,
                        quantity: String): Flow<ResponseWrapper<Int>> {
        return repository.modifyOrder(uniqueOrderID, stopPrice, price, quantity).map {
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
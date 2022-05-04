package com.finsol.tech.domain.marketdata

import com.finsol.tech.data.model.GenericMessageResponse
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.domain.LoginResponseDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterData @Inject constructor(private val repository: LoginResponseDataRepository) {
    suspend fun execute(
        name: String,
        email: String,
        phone: String
    ): Flow<ResponseWrapper<GenericMessageResponse>> {
        return repository.register(name, email, phone)
    }
}

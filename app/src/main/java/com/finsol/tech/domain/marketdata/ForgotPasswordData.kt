package com.finsol.tech.domain.marketdata

import com.finsol.tech.data.model.GenericMessageResponse
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.domain.LoginResponseDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ForgotPasswordData @Inject constructor(private val repository: LoginResponseDataRepository) {
    suspend fun execute(
        userName: String
    ): Flow<ResponseWrapper<GenericMessageResponse>> {
        return repository.forgotPassword(userName)
    }
}

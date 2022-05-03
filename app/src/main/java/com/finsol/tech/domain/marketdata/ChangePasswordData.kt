package com.finsol.tech.domain.marketdata

import com.finsol.tech.data.model.GenericMessageResponse
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.domain.LoginResponseDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChangePasswordData @Inject constructor(private val repository: LoginResponseDataRepository) {
    suspend fun execute(
        userID: String,
        userName: String,
        newPassword: String
    ): Flow<ResponseWrapper<GenericMessageResponse>> {
        return repository.changePassword(userID, userName, newPassword)
    }
}

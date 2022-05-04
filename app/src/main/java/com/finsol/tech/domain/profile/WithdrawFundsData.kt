package com.finsol.tech.domain.profile

import com.finsol.tech.data.model.GenericMessageResponse
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.domain.LoginResponseDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WithdrawFundsData @Inject constructor(private val repository: LoginResponseDataRepository) {
    suspend fun execute(
        userName: String
    ): Flow<ResponseWrapper<GenericMessageResponse>> {
        return repository.withdrawFunds(userName)
    }
}

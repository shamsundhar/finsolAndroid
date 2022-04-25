package com.finsol.tech.domain

import com.finsol.tech.data.model.LoginResponse
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.data.model.Market
import kotlinx.coroutines.flow.Flow

interface LoginResponseDataRepository {
    suspend fun getLoginData(userID: String, password: String): Flow<ResponseWrapper<LoginResponse>>
}
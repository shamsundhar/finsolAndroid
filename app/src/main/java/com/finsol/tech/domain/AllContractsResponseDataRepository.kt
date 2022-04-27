package com.finsol.tech.domain

import com.finsol.tech.data.model.*
import kotlinx.coroutines.flow.Flow

interface AllContractsResponseDataRepository {
    suspend fun getAllContractsData(userID: String): Flow<ResponseWrapper<GetAllContractsResponse>>
}
package com.finsol.tech.domain.orders

import com.finsol.tech.data.model.PendingOrderModel
import com.finsol.tech.data.model.PendingOrderResponse
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.data.model.toProfileDomain
import com.finsol.tech.domain.LoginResponseDataRepository
import com.finsol.tech.domain.model.ProfileResponseDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPendingOrdersData @Inject constructor(private val repository : LoginResponseDataRepository) {
    suspend fun execute(userID: String): Flow<ResponseWrapper<Array<PendingOrderModel>>> {
        return repository.getPendingOrdersData(userID)
    }
}
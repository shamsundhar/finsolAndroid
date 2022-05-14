package com.finsol.tech.domain.marketdata

import com.finsol.tech.data.model.ExchangeEnumModel
import com.finsol.tech.data.model.ExchangeOptionsModel
import com.finsol.tech.data.model.GenericMessageResponse
import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.domain.LoginResponseDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExchangeOptionsData @Inject constructor(private val repository: LoginResponseDataRepository) {
    suspend fun execute(): Flow<ResponseWrapper<Array<ExchangeOptionsModel>>> {
        return repository.getExchangeOptions()
    }
}

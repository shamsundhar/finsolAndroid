package com.finsol.tech.domain.contracts

import com.finsol.tech.data.model.*
import com.finsol.tech.domain.LoginResponseDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AddToWatchListData @Inject constructor(private val repository : LoginResponseDataRepository) {
    suspend fun execute(userID: String, watchListNumber:String, securityID:String): Flow<ResponseWrapper<GenericMessageResponse>> {
        return repository.addToWatchList(userID, watchListNumber, securityID)
    }
}
package com.finsol.tech.domain.profile

import com.finsol.tech.data.model.ResponseWrapper
import com.finsol.tech.data.model.toLoginDomain
import com.finsol.tech.data.model.toMarketDomain
import com.finsol.tech.data.model.toProfileDomain
import com.finsol.tech.domain.LoginResponseDataRepository
import com.finsol.tech.domain.MarketDataRepository
import com.finsol.tech.domain.ProfileResponseDataRepository
import com.finsol.tech.domain.model.LoginResponseDomainModel
import com.finsol.tech.domain.model.MarketDomainModel
import com.finsol.tech.domain.model.ProfileResponseDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetProfileData @Inject constructor(private val repository : ProfileResponseDataRepository) {
    suspend fun execute(userID: String): Flow<ResponseWrapper<ProfileResponseDomainModel>> {
        return repository.getProfileData(userID).map {
            when(it){
                is ResponseWrapper.Success -> {
                    ResponseWrapper.Success(it.value.toProfileDomain())
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
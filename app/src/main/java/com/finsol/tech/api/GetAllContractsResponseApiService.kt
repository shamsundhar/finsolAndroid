package com.finsol.tech.api

import com.finsol.tech.data.model.GetAllContractsResponse
import com.finsol.tech.data.model.LoginResponse
import com.finsol.tech.data.model.Market
import com.finsol.tech.data.model.ProfileResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GetAllContractsResponseApiService {

        @GET("QT_Mobile_Host/GetAllContracts")
        suspend fun getAllContractsResponse(@Query("userID") userName: String) : GetAllContractsResponse

}
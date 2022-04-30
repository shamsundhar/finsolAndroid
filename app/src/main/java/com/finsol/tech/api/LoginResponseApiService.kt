package com.finsol.tech.api

import com.finsol.tech.data.model.GetAllContractsResponse
import com.finsol.tech.data.model.LoginResponse
import com.finsol.tech.data.model.Market
import com.finsol.tech.data.model.ProfileResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LoginResponseApiService {

        @GET("QT_Mobile_Host/UserLogin")
        suspend fun getLoginResponse(@Query("userName") userName: String,
                                  @Query("password") password: String) : LoginResponse
    @GET("QT_Mobile_Host/GetUserProfile")
    suspend fun getProfileResponse(@Query("userID") userName: String) : ProfileResponse

    @GET("QT_Mobile_Host/GetAllContracts")
    suspend fun getAllContractsResponse(@Query("userID") userName: String) : GetAllContractsResponse


}
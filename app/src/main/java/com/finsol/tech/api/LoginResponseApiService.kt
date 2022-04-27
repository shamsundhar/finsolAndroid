package com.finsol.tech.api

import com.finsol.tech.data.model.LoginResponse
import com.finsol.tech.data.model.Market
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LoginResponseApiService {

        @GET("QT_Mobile_Host/UserLogin")
        suspend fun getLoginResponse(@Query("userName") userName: String,
                                  @Query("password") password: String) : LoginResponse

}
//package com.finsol.tech.api
//
//import com.finsol.tech.data.model.LoginResponse
//import com.finsol.tech.data.model.Market
//import com.finsol.tech.data.model.ProfileResponse
//import retrofit2.http.GET
//import retrofit2.http.Path
//import retrofit2.http.Query
//
//interface ProfileResponseApiService {
//
//        @GET("QT_Mobile_Host/GetUserProfile")
//        suspend fun getProfileResponse(@Query("userID") userName: String) : ProfileResponse
//
//}
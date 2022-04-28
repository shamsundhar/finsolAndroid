package com.finsol.tech.api

import com.finsol.tech.data.model.PortfolioResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PortfolioResponseApiService {

        @GET("QT_Mobile_Host/GetPortfolio")
        suspend fun getPortfolioResponse(@Query("userID") userID: Int) : PortfolioResponse

}
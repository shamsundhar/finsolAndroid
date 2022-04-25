package com.finsol.tech.api

import com.finsol.tech.data.model.Market
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MarketDataApiService {

        @GET("/GetMarketData")
        suspend fun getMarketData(@Query("securityID") securityID: String,
                                  @Query("exchangeName") exchangeName: String) : Market

}
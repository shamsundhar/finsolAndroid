package com.finsol.tech.data.model

import com.google.gson.annotations.SerializedName

data class GetAllContractsResponse(
    @SerializedName("AllContracts") var allContracts : List<Contracts>,
    @SerializedName("Watchlist1") val watchlist1 : List<Contracts>,
    @SerializedName("Watchlist2") val watchlist2 : List<Contracts>,
    @SerializedName("Watchlist3") val watchlist3 : List<Contracts>
)

data class Contracts(
    @SerializedName("ClosePrice") val closePrice : Int,
    @SerializedName("DisplayName") val displayName : String,
    @SerializedName("ExchangeName") val exchangeName : String,
    @SerializedName("Expiry") val expiry : Int,
    @SerializedName("LTP") val lTP : Int,
    @SerializedName("LotSize") val lotSize : Int,
    @SerializedName("MaturityDay") val maturityDay : Int,
    @SerializedName("SecurityID") val securityID : Int,
    @SerializedName("SecurityType") val securityType : String,
    @SerializedName("SymbolName") val symbolName : String,
    @SerializedName("TickSize") val tickSize : Float,
    var isAddedToWatchList: Boolean
)
//Integer closePrice, String displayName, String exchangeName, String expiry, Integer ltp, Integer lotSize, Integer maturityDay, String securityID, String securityType, String symbolName, Float tickSize
package com.finsol.tech.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetAllContractsResponse(
    @SerializedName("AllContracts") var allContracts : List<Contracts>,
    @SerializedName("Watchlist1") val watchlist1 : List<Contracts>,
    @SerializedName("Watchlist2") val watchlist2 : List<Contracts>,
    @SerializedName("Watchlist3") val watchlist3 : List<Contracts>
):Parcelable

@Parcelize
data class Contracts(
    @SerializedName("ClosePrice") var closePrice : String = "",
    @SerializedName("DisplayName") val displayName : String = "",
    @SerializedName("ExchangeName") val exchangeName : String = "",
    @SerializedName("Expiry") val expiry : String = "",
    @SerializedName("LTP") val lTP : String = "",
    @SerializedName("LotSize") val lotSize : String = "",
    @SerializedName("MaturityDay") val maturityDay : String = "",
    @SerializedName("SecurityID") val securityID : String = "",
    @SerializedName("SecurityType") val securityType : String = "",
    @SerializedName("SymbolName") val symbolName : String = "",
    @SerializedName("TickSize") val tickSize : String = "",
    var isAddedToWatchList: Boolean = false,
    var updatedTime : String = ""
) : Parcelable
//Integer closePrice, String displayName, String exchangeName, String expiry, Integer ltp, Integer lotSize, Integer maturityDay, String securityID, String securityType, String symbolName, Float tickSize
package com.finsol.tech.data.model
import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class PortfolioResponse(val GetPortfolioResult: ArrayList<PortfolioData>):Parcelable
@Parcelize
data class PortfolioData(
    @SerializedName("AccountID") val accountID: Int,
    @SerializedName("AccountName") val accountName: String,
    @SerializedName("AvgBuyPrice") val avgBuyPrice: Int,
    @SerializedName("AvgSellPrice") val avgSellPrice: Int,
    @SerializedName("CloseingPrice") val closeingPrice: Int,
    @SerializedName("ContractYear") val contractYear: Int,
    @SerializedName("Ctcl") val ctcl: String,
    @SerializedName("CumulativePNL") val cumulativePNL: Int,
    @SerializedName("CurrentPNL") val currentPNL: Int,
    @SerializedName("CurrentPrice") val currentPrice: Int,
    @SerializedName("Date_Time") val date_Time: String,
    @SerializedName("ExchangeName") val exchangeName: Int,
    @SerializedName("IntrradayPNL") val intrradayPNL: Int,
    @SerializedName("MarginMoney") val marginMoney: Int,
    @SerializedName("NetPosition") val netPosition: Int,
    @SerializedName("OpeningQty") val openingQty: Int,
    @SerializedName("ProductSymbol") val productSymbol: String,
    @SerializedName("SecurityID") val securityID: Int,
    @SerializedName("TotalQtyBuy") val totalQtyBuy: Int,
    @SerializedName("TotalQtySell") val totalQtySell: Int,
    @SerializedName("UpdateBy") val updateBy: String,
    @SerializedName("UserID") val userID: Int,
    @SerializedName("Username") val username: String
):Parcelable

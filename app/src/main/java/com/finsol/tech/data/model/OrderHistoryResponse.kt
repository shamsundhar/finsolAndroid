package com.finsol.tech.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class OrderHistoryResponse(val orderHistoryList : Array<OrderHistoryModel>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OrderHistoryResponse

        if (!orderHistoryList.contentEquals(other.orderHistoryList)) return false

        return true
    }

    override fun hashCode(): Int {
        return orderHistoryList.contentHashCode()
    }
}

@Parcelize
data class OrderHistoryModel(val AccountID: Int, val AccountName:String, val AlgoName:String,
                             val CTCLID:String, val ClOrdID:String, val ContractYear:String,
                             val ExchangeMessage:String, val ExchangeOderID:String,
                             val ExchangeTradingID:String, val ExchangeTransactTime:String, val Exchange_Name:Int,
                             val Market_Type:Int, val OrderQty:Int, val OrderStatus:String,
                             val Order_Type:Int, val Price:Int, val QTOrderID:String, val QTRecieveTime:String, val QTTradeID:Int,
                             val SecurityID:String, val SecurityType:String, val SenderComID:String, val ShanghaiOrdIND:String,
                             val ShanghaiOrdValue:String, val Symbol_Name:String,
                             val UserID:Int, val UserName:String, var LTP: String, var updatedTime : String = "",
                             var tickSize : String = "", var lotSize : String = "",
                             var closePrice : Float, var maturityDay : String = "",
                             var exchangeNameString: String = "") : Parcelable



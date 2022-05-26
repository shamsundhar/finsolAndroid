package com.finsol.tech.data.model

import com.finsol.tech.domain.model.LoginResponseDomainModel
import com.finsol.tech.domain.model.ProfileResponseDomainModel
import com.finsol.tech.util.Utilities
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray


//fun Market.toMarketDomain() = MarketDomainModel(GetMarketDataResult = GetMarketDataResult)

fun LoginResponse.toLoginDomain() =
    LoginResponseDomainModel(status = status, message = message, userID = userID)

fun ProfileResponse.toProfileDomain() = ProfileResponseDomainModel(
    emailid = emailid,
    firstname = firstname,
    gender = gender,
    lastname = lastname,
    name = name,
    phone = phone
)

fun String.toMarketData(): Market {
    val jsonData = JSONArray(this)
    val securityID: String = jsonData[0] as String
    val askPriceArray: ArrayList<ArrayList<Float>> = Gson().fromJson(
        jsonData[1].toString(),
        object : TypeToken<ArrayList<ArrayList<Float>>>() {}.getType()
    )
    val bidPriceArray: ArrayList<ArrayList<Float>> = Gson().fromJson(
        jsonData[2].toString(),
        object : TypeToken<ArrayList<ArrayList<Float>>>() {}.getType()
    )
    val LTP: String = jsonData[3].toString()
    val OpenPrice: String = jsonData[4].toString()
    val HighPrice: String = jsonData[5].toString()
    val LowPrice: String = jsonData[6].toString()
    val ClosePrice: String = jsonData[7].toString()
    val Volume: String = jsonData[8].toString()
    val OpenInterest: String = jsonData[9].toString()
    val DPRHigh: String = jsonData[10].toString()
    val DPRLow: String = jsonData[11].toString()

    return Market(
        securityID,
        askPriceArray,
        bidPriceArray,
        LTP,
        OpenPrice,
        HighPrice,
        LowPrice,
        ClosePrice,
        Volume, OpenInterest, DPRHigh, DPRLow
    )

}

fun GetAllContractsResponse.maskResponse(): GetAllContractsResponse {
    val time = Utilities.getCurrentTime()
    this.watchlist1.map {
        it.updatedTime = time
    }
    this.watchlist2.map {
        it.updatedTime = time
    }
    this.watchlist3.map {
        it.updatedTime = time
    }
    return this
}

fun Array<OrderHistoryModel>.maskOrderHistoryModel(): Array<OrderHistoryModel> {
    this.map {
        it.LTP = if(it.LTP.isNullOrBlank()){"-"} else{it.LTP}
    }
    return this
}

fun OrderHistoryModel.toNonNullOrderHistoryModel(): OrderHistoryModel {

    val AccountID: Int = if (this.AccountID == null) 0 else AccountID
    val AccountName: String = if (this.AccountName == null) "" else AccountName
    val AlgoName: String = if (this.AlgoName == null) "" else AlgoName
    val CTCLID: String = if (this.CTCLID == null) "" else CTCLID
    val ClOrdID: String = if (this.ClOrdID == null) "" else ClOrdID
    val ContractYear: String = if (this.ContractYear == null) "" else ContractYear
    val ExchangeMessage: String = if (this.ExchangeMessage == null) "" else ExchangeMessage
    val ExchangeOderID: String = if (this.ExchangeOderID == null) "" else ExchangeOderID
    val ExchangeTradingID: String = if (this.ExchangeTradingID == null) "" else ExchangeTradingID
    val ExchangeTransactTime: String =
        if (this.ExchangeTransactTime == null) "" else ExchangeTransactTime
    val Exchange_Name: Int = if (this.Exchange_Name == null) 0 else Exchange_Name
    val Market_Type: Int = if (this.Market_Type == null) 0 else Market_Type
    val OrderQty: Int = if (this.OrderQty == null) 0 else OrderQty
    val OrderStatus: String = if (this.OrderStatus == null) "" else OrderStatus
    val Order_Type: Int = if (this.Order_Type == null) 0 else Order_Type
    val Price: Int = if (this.Price == null) 0 else Price
    val QTOrderID: String = if (this.QTOrderID == null) "" else QTOrderID
    val QTRecieveTime: String = if (this.QTRecieveTime == null) "" else QTRecieveTime
    val QTTradeID: Int = if (this.QTTradeID == null) 0 else QTTradeID
    val SecurityID: String = if (this.SecurityID == null) "" else SecurityID
    val SecurityType: String = if (this.SecurityType == null) "" else SecurityType
    val SenderComID: String = if (this.SenderComID == null) "" else SenderComID
    val ShanghaiOrdIND: String = if (this.ShanghaiOrdIND == null) "" else ShanghaiOrdIND
    val ShanghaiOrdValue: String = if (this.ShanghaiOrdValue == null) "" else ShanghaiOrdValue
    val Symbol_Name: String = if (this.Symbol_Name == null) "" else Symbol_Name
    val UserID: Int = if (this.UserID == null) 0 else UserID
    val UserName: String = if (this.UserName == null) "" else UserName
    var LTP: String = if (this.LTP == null) "" else LTP

    return OrderHistoryModel(
        AccountID = AccountID,
        AccountName = AccountName,
        CTCLID = CTCLID,
        AlgoName = AlgoName,
        ClOrdID = ClOrdID,
        ContractYear = ContractYear,
        ExchangeMessage = ExchangeMessage,
        ExchangeOderID = ExchangeOderID,
        ExchangeTradingID = ExchangeTradingID,
        ExchangeTransactTime = ExchangeTransactTime,
        Exchange_Name = Exchange_Name,
        Market_Type = Market_Type,
        OrderQty = OrderQty,
        OrderStatus = OrderStatus,
        Order_Type = Order_Type,
        Price = Price,
        QTOrderID = QTOrderID,
        QTRecieveTime = QTRecieveTime,
        QTTradeID = QTTradeID,
        SecurityID = SecurityID,
        SecurityType = SecurityType,
        SenderComID = SenderComID,
        ShanghaiOrdIND = ShanghaiOrdIND,
        ShanghaiOrdValue = ShanghaiOrdValue,
        Symbol_Name = Symbol_Name,
        UserID = UserID,
        UserName = UserName,
        LTP = LTP
    )

}


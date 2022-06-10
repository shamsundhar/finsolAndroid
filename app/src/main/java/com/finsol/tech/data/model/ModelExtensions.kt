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

//fun Array<OrderHistoryModel>.maskOrderHistoryModel(): Array<OrderHistoryModel> {
//    this.map {
//        it.LTP = if(it.LTP.isNullOrBlank()){"-"} else{it.LTP}
//    }
//    return this
//}

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

fun PendingOrderModel.toNonNullModel(): PendingOrderModel {
    val AccountID: Int = if (this.AccountID == null) 0 else this.AccountID
    val AccountName: String = if (this.AccountName == null) "" else this.AccountName
    val AlgoIdentifierKey: String =
        if (this.AlgoIdentifierKey == null) "" else this.AlgoIdentifierKey
    val AlgoName: String = if (this.AlgoName == null) "" else this.AlgoName
    val AlgoSide: Int = if (this.AlgoSide == null) 0 else this.AlgoSide
    val AveragePrice: Int = if (this.AveragePrice == null) 0 else this.AveragePrice
    val BeginString: String = if (this.BeginString == null) "" else this.BeginString
    val CTCLID: String = if (this.CTCLID == null) "" else this.CTCLID
    val CTCLName: String = if (this.CTCLName == null) "" else this.CTCLName
    val ChangeQty: Int = if (this.ChangeQty == null) 0 else this.ChangeQty
    val ClOrdID: String = if (this.ClOrdID == null) "" else this.ClOrdID
    val ContractYear: String = if (this.ContractYear == null) "" else this.ContractYear
    val CustomerOrFirm: Int = if (this.CustomerOrFirm == null) 0 else this.CustomerOrFirm
    val DummyTimeVariable: String =
        if (this.DummyTimeVariable == null) "" else this.DummyTimeVariable
    val ErrorMessage: String = if (this.ErrorMessage == null) "" else this.ErrorMessage
    val ExceuteQty: Int = if (this.ExceuteQty == null) 0 else this.ExceuteQty
    val ExchangeMessage: String = if (this.ExchangeMessage == null) "" else this.ExchangeMessage
    val ExchangeOderID: String = if (this.ExchangeOderID == null) "" else this.ExchangeOderID
    val ExchangeTradingID: String =
        if (this.ExchangeTradingID == null) "" else this.ExchangeTradingID
    val ExchangeTransactTime: String =
        if (this.ExchangeTransactTime == null) "" else this.ExchangeTransactTime
    val Exchange_Name: Int = if (this.Exchange_Name == null) 0 else this.Exchange_Name
    val ExternalAlgoActionID: Int =
        if (this.ExternalAlgoActionID == null) 0 else this.ExternalAlgoActionID
    val ExternalAlgoAgentName: String =
        if (this.ExternalAlgoAgentName == null) "" else this.ExternalAlgoAgentName
    val ExternalEngineName: String =
        if (this.ExternalEngineName == null) "" else this.ExternalEngineName
    val FilledQty: Int = if (this.FilledQty == null) 0 else this.FilledQty
    val Flag: Boolean = if (this.Flag == null) false else this.Flag
    val GTDExpiryDate: String = if (this.GTDExpiryDate == null) "" else this.GTDExpiryDate
    val HandInt: Int = if (this.HandInt == null) 0 else this.AccountID
    val IDSource: Int = if (this.IDSource == null) 0 else this.IDSource
    val IsExternalAlgoOrder: Boolean =
        if (this.IsExternalAlgoOrder == null) false else this.IsExternalAlgoOrder
    val IsSemiManualOrder: Boolean =
        if (this.IsSemiManualOrder == null) false else this.IsSemiManualOrder
    val IsSquareOffAlgoOrder: Boolean =
        if (this.IsSquareOffAlgoOrder == null) false else this.IsSquareOffAlgoOrder
    val LastModifiedTime: String = if (this.LastModifiedTime == null) "" else this.LastModifiedTime
    val LeftQty: Int = if (this.LeftQty == null) 0 else this.LeftQty
    val Market_Type: Int = if (this.Market_Type == null) 0 else this.Market_Type
    val MaturityDay: Int = if (this.MaturityDay == null) 0 else this.MaturityDay
    val MessageType: String = if (this.MessageType == null) "" else this.MessageType
    val OrderDayType: Int = if (this.OrderDayType == null) 0 else this.OrderDayType
    val OrderQty: Int = if (this.OrderQty == null) 0 else this.OrderQty
    val OrderStatus: String = if (this.OrderStatus == null) "" else this.OrderStatus
    val OrderTime: String = if (this.OrderTime == null) "" else this.OrderTime
    val Order_Type: Int = if (this.Order_Type == null) 0 else this.Order_Type
    val OrginatorClOrdID: String = if (this.OrginatorClOrdID == null) "" else this.OrginatorClOrdID
    val Price: Int = if (this.Price == null) 0 else this.Price
    val PriceSend: Double = if (this.PriceSend == null) 0.0 else this.PriceSend
    val QTOrderID: String = if (this.QTOrderID == null) "" else this.QTOrderID
    val QTOrderValue: String = if (this.QTOrderValue == null) "" else this.QTOrderValue
    val QTOrderValue_d: String = if (this.QTOrderValue_d == null) "" else this.QTOrderValue_d
    val QTTradeID: Int = if (this.QTTradeID == null) 0 else this.QTTradeID
    val RequestId: String = if (this.RequestId == null) "" else this.RequestId
    val SHFECancelFlag: Int = if (this.SHFECancelFlag == null) 0 else this.SHFECancelFlag
    val SHFEReplacePrice: String = if (this.SHFEReplacePrice == null) "" else this.SHFEReplacePrice
    val SecurityID: String = if (this.SecurityID == null) "" else this.SecurityID
    val SecurityType: String = if (this.SecurityType == null) "" else this.SecurityType
    val SenderComID: String = if (this.SenderComID == null) "" else this.SenderComID
    val ShanghaiOrdIND: String = if (this.ShanghaiOrdIND == null) "" else this.ShanghaiOrdIND
    val ShanghaiOrdValue: String = if (this.ShanghaiOrdValue == null) "" else this.ShanghaiOrdValue
    val StatusBit: Int = if (this.StatusBit == null) 0 else this.StatusBit
    val StopPrice: Double = if (this.StopPrice == null) 0.0 else this.StopPrice
    val Symbol_Name: String = if (this.Symbol_Name == null) "" else this.Symbol_Name
    val TargetCompId: String = if (this.TargetCompId == null) "" else this.TargetCompId
    val tickSize: String = if (this.tickSize == null) "" else this.tickSize
    val TmpExceuteQty: Int = if (this.TmpExceuteQty == null) 0 else this.TmpExceuteQty
    val TmpOrderQty: Int = if (this.TmpOrderQty == null) 0 else this.TmpOrderQty
    val TotalModification: Int = if (this.TotalModification == null) 0 else this.TotalModification
    val TrigPrice: Int = if (this.TrigPrice == null) 0 else this.TrigPrice
    val UniqueEngineOrderID: String =
        if (this.UniqueEngineOrderID == null) "" else this.UniqueEngineOrderID
    val UserID: Int = if (this.UserID == null) 0 else this.UserID
    val UserName: String = if (this.UserName == null) "" else this.UserName
    val WeightedAvgPrice: Int = if (this.WeightedAvgPrice == null) 0 else this.WeightedAvgPrice
    val WorkQty: Int = if (this.WorkQty == null) 0 else this.WorkQty
    val algoLotChangeFactor: Int =
        if (this.algoLotChangeFactor == null) 0 else this.algoLotChangeFactor
    val algoToCancel: Boolean = if (this.algoToCancel == null) false else this.algoToCancel
    var LTP: String = if (this.LTP == null) "" else this.LTP

    return PendingOrderModel(
        AccountID = AccountID,
        AccountName = AccountName,
        AlgoIdentifierKey = AlgoIdentifierKey,
        AlgoName = AlgoName,
        AlgoSide = AlgoSide,
        AveragePrice = AveragePrice,
        BeginString = BeginString,
        CTCLID = CTCLID,
        CTCLName = CTCLName,
        ChangeQty = ChangeQty,
        ClOrdID = ClOrdID,
        ContractYear = ContractYear,
        CustomerOrFirm = CustomerOrFirm,
        DummyTimeVariable = DummyTimeVariable,
        ErrorMessage = ErrorMessage,
        ExceuteQty = ExceuteQty,
        ExchangeMessage = ExchangeMessage,
        ExchangeOderID = ExchangeOderID,
        ExchangeTradingID = ExchangeTradingID,
        ExchangeTransactTime = ExchangeTransactTime,
        Exchange_Name = Exchange_Name,
        ExternalAlgoActionID = ExternalAlgoActionID,
        ExternalAlgoAgentName = ExternalAlgoAgentName,
        ExternalEngineName = ExternalEngineName,
        FilledQty = FilledQty,
        Flag = Flag,
        GTDExpiryDate = GTDExpiryDate,
        HandInt = HandInt,
        IDSource = IDSource,
        IsExternalAlgoOrder = IsExternalAlgoOrder,
        IsSemiManualOrder = IsSemiManualOrder,
        IsSquareOffAlgoOrder = IsSquareOffAlgoOrder,
        LastModifiedTime = LastModifiedTime,
        LeftQty = LeftQty,
        Market_Type = Market_Type,
        MaturityDay = MaturityDay,
        MessageType = MessageType,
        OrderDayType = OrderDayType,
        OrderQty = OrderQty,
        OrderStatus = OrderStatus,
        OrderTime = OrderTime,
        Order_Type = Order_Type,
        OrginatorClOrdID = OrginatorClOrdID,
        Price = Price,
        PriceSend = PriceSend,
        QTOrderID = QTOrderID,
        QTOrderValue = QTOrderValue,
        QTOrderValue_d = QTOrderValue_d,
        QTTradeID = QTTradeID,
        RequestId = RequestId,
        SHFECancelFlag = SHFECancelFlag,
        SHFEReplacePrice = SHFEReplacePrice,
        SecurityID = SecurityID,
        SecurityType = SecurityType,
        SenderComID = SenderComID,
        ShanghaiOrdIND = ShanghaiOrdIND,
        ShanghaiOrdValue = ShanghaiOrdValue,
        StatusBit = StatusBit,
        StopPrice = StopPrice,
        Symbol_Name = Symbol_Name,
        TargetCompId = TargetCompId,
        tickSize = tickSize,
        TmpExceuteQty = TmpExceuteQty,
        TmpOrderQty = TmpOrderQty,
        TotalModification = TotalModification,
        TrigPrice = TrigPrice,
        UniqueEngineOrderID = UniqueEngineOrderID,
        UserID = UserID,
        UserName = UserName,
        WeightedAvgPrice = WeightedAvgPrice,
        WorkQty = WorkQty,
        algoLotChangeFactor = algoLotChangeFactor,
        algoToCancel = algoToCancel,
        LTP = LTP
    )
}


fun PortfolioData.toNonNullModel(): PortfolioData {

    val accountID: Int = if (this.accountID == null) 0 else this.accountID
    val accountName: String = if (this.accountName == null) "" else this.accountName
    val avgBuyPrice: Double = if(this.avgBuyPrice == null) 0.0 else this.avgBuyPrice
    val avgSellPrice: Double = if(this.avgSellPrice == null) 0.0 else this.avgSellPrice
    val closeingPrice: Int = if(this.closeingPrice == null) 0 else this.closeingPrice
    val contractYear: Int = if(this.contractYear == null) 0 else this.contractYear
    val ctcl: String = if(this.ctcl == null) "" else this.ctcl
    val cumulativePNL: Double = if(this.cumulativePNL == null) 0.0 else this.cumulativePNL
    val currentPNL: Double = if(this.currentPNL == null) 0.0 else this.currentPNL
    val currentPrice: Int = if(this.currentPrice == null) 0 else this.currentPrice
    val date_Time: String = if(this.date_Time == null) "" else this.date_Time
    val exchangeName: Int = if(this.exchangeName == null) 0 else this.exchangeName
    val intrradayPNL: Int = if(this.intrradayPNL == null) 0 else this.intrradayPNL
    val marginMoney: Int = if(this.marginMoney == null) 0 else this.marginMoney
    val netPosition: Int = if(this.netPosition == null) 0 else this.netPosition
    val openingQty: Int = if(this.openingQty == null) 0 else this.openingQty
    val productSymbol: String = if(this.productSymbol == null) "" else this.productSymbol
    val securityID: Int = if(this.securityID == null) 0 else this.securityID
    val totalQtyBuy: Int = if(this.totalQtyBuy == null) 0 else this.totalQtyBuy
    val totalQtySell: Int = if(this.totalQtySell == null) 0 else this.totalQtySell
    val updateBy: String = if(this.updateBy == null) "" else this.updateBy
    val userID: Int = if(this.userID == null) 0 else this.userID
    val username: String = if(this.username == null) "" else this.username
    val LTP: String = if(this.LTP == null) "" else this.LTP
    val LTPChangePercent: String = if(this.LTPChangePercent == null) "" else this.LTPChangePercent
    val exchangeNameString: String = if(this.exchangeNameString == null) "" else this.exchangeNameString
    val price: String = if(this.price == null) "" else this.price
    val quantity: String = if(this.quantity == null) "" else this.quantity

    return PortfolioData(
        accountID = accountID,
        accountName = accountName,
        avgBuyPrice = avgBuyPrice,
        avgSellPrice = avgSellPrice,
        closeingPrice = closeingPrice,
        contractYear = contractYear,
        ctcl = ctcl,
        cumulativePNL = cumulativePNL,
        currentPNL = currentPNL,
        currentPrice = currentPrice,
        date_Time = date_Time,
        exchangeName = exchangeName,
        intrradayPNL = intrradayPNL,
        marginMoney = marginMoney,
        netPosition = netPosition,
        openingQty = openingQty,
        productSymbol = productSymbol,
        securityID = securityID,
        totalQtyBuy = totalQtyBuy,
        totalQtySell = totalQtySell,
        updateBy = updateBy,
        userID = userID,
        username = username,
        LTP = LTP,
        LTPChangePercent = LTPChangePercent,
        exchangeNameString = exchangeNameString,
        price = price,
        quantity = quantity
    )
}


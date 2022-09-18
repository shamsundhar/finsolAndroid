package com.finsol.tech.data.model

import com.google.gson.annotations.SerializedName

data class RejectedCancelledOrdersResponse(
    @SerializedName("AccountID") var AccountID: Int? = null,
    @SerializedName("AccountName") var AccountName: String? = null,
    @SerializedName("AlgoIdentifierKey") var AlgoIdentifierKey: String? = null,
    @SerializedName("AlgoName") var AlgoName: String? = null,
    @SerializedName("AlgoSide") var AlgoSide: Int? = null,
    @SerializedName("AveragePrice") var AveragePrice: Double? = null,
    @SerializedName("BeginString") var BeginString: String? = null,
    @SerializedName("CTCLID") var CTCLID: String? = null,
    @SerializedName("CTCLName") var CTCLName: String? = null,
    @SerializedName("ChangeQty") var ChangeQty: Int? = null,
    @SerializedName("ClOrdID") var ClOrdID: String? = null,
    @SerializedName("ContractYear") var ContractYear: String? = null,
    @SerializedName("CustomerOrFirm") var CustomerOrFirm: Int? = null,
    @SerializedName("DiscloseQty") var DiscloseQty: Int? = null,
    @SerializedName("DummyTimeVariable") var DummyTimeVariable: String? = null,
    @SerializedName("ErrorMessage") var ErrorMessage: String? = null,
    @SerializedName("ExceuteQty") var ExceuteQty: Int? = null,
    @SerializedName("ExchangeMessage") var ExchangeMessage: String? = null,
    @SerializedName("ExchangeOderID") var ExchangeOderID: String? = null,
    @SerializedName("ExchangeTradingID") var ExchangeTradingID: String? = null,
    @SerializedName("ExchangeTransactTime") var ExchangeTransactTime: String? = null,
    @SerializedName("Exchange_Name") var ExchangeName: Int? = null,
    @SerializedName("ExternalAlgoActionID") var ExternalAlgoActionID: Int? = null,
    @SerializedName("ExternalAlgoAgentName") var ExternalAlgoAgentName: String? = null,
    @SerializedName("ExternalEngineName") var ExternalEngineName: String? = null,
    @SerializedName("FilledQty") var FilledQty: Int? = null,
    @SerializedName("Flag") var Flag: Boolean? = null,
    @SerializedName("GTDExpiryDate") var GTDExpiryDate: String? = null,
    @SerializedName("HandInt") var HandInt: Int? = null,
    @SerializedName("IDSource") var IDSource: Int? = null,
    @SerializedName("IsExternalAlgoOrder") var IsExternalAlgoOrder: Boolean? = null,
    @SerializedName("IsSemiManualOrder") var IsSemiManualOrder: Boolean? = null,
    @SerializedName("IsSquareOffAlgoOrder") var IsSquareOffAlgoOrder: Boolean? = null,
    @SerializedName("LastModifiedTime") var LastModifiedTime: String? = null,
    @SerializedName("LeftQty") var LeftQty: Int? = null,
    @SerializedName("Market_Type") var MarketType: Int? = null,
    @SerializedName("MaturityDay") var MaturityDay: Int? = null,
    @SerializedName("MessageType") var MessageType: String? = null,
    @SerializedName("OrderDayType") var OrderDayType: Int? = null,
    @SerializedName("OrderQty") var OrderQty: Int? = null,
    @SerializedName("OrderStatus") var OrderStatus: String? = null,
    @SerializedName("OrderTime") var OrderTime: String? = null,
    @SerializedName("Order_Type") var OrderType: Int? = null,
    @SerializedName("OrginatorClOrdID") var OrginatorClOrdID: String? = null,
    @SerializedName("Price") var Price: Double? = null,
    @SerializedName("PriceSend") var PriceSend: Double? = null,
    @SerializedName("QTOrderID") var QTOrderID: String? = null,
    @SerializedName("QTOrderValue") var QTOrderValue: String? = null,
    @SerializedName("QTOrderValue_d") var QTOrderValueD: String? = null,
    @SerializedName("QTTradeID") var QTTradeID: Int? = null,
    @SerializedName("RequestId") var RequestId: String? = null,
    @SerializedName("SHFECancelFlag") var SHFECancelFlag: Int? = null,
    @SerializedName("SHFEReplacePrice") var SHFEReplacePrice: String? = null,
    @SerializedName("SecurityID") var SecurityID: String? = null,
    @SerializedName("SecurityType") var SecurityType: String? = null,
    @SerializedName("SenderComID") var SenderComID: String? = null,
    @SerializedName("ShanghaiOrdIND") var ShanghaiOrdIND: String? = null,
    @SerializedName("ShanghaiOrdValue") var ShanghaiOrdValue: String? = null,
    @SerializedName("StatusBit") var StatusBit: Int? = null,
    @SerializedName("StopPrice") var StopPrice: Float? = null,
    @SerializedName("Symbol_Name") var SymbolName: String? = null,
    @SerializedName("TargetCompId") var TargetCompId: String? = null,
    @SerializedName("TickSize") var TickSize: String? = null,
    @SerializedName("TmpExceuteQty") var TmpExceuteQty: Int? = null,
    @SerializedName("TmpOrderQty") var TmpOrderQty: Int? = null,
    @SerializedName("TotalModification") var TotalModification: Int? = null,
    @SerializedName("TrigPrice") var TrigPrice: Double? = null,
    @SerializedName("UniqueEngineOrderID") var UniqueEngineOrderID: String? = null,
    @SerializedName("UserID") var UserID: Int? = null,
    @SerializedName("UserName") var UserName: String? = null,
    @SerializedName("WeightedAvgPrice") var WeightedAvgPrice: Double? = null,
    @SerializedName("WorkQty") var WorkQty: Int? = null,
    @SerializedName("algoLotChangeFactor") var algoLotChangeFactor: Int? = null,
    @SerializedName("algoToCancel") var algoToCancel: Boolean? = null

)

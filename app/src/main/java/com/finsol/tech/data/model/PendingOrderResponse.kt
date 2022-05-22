package com.finsol.tech.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class PendingOrderResponse(val pendingOrdersList : List<PendingOrderModel>) : Parcelable
@Parcelize
data class PendingOrderModel(val AccountID: Int, val AccountName:String, val AlgoIdentifierKey:String, val AlgoName: String,
                             val AlgoSide:Int, val AveragePrice:Int, val BeginString:String, val CTCLID:String,
                             val CTCLName:String, val ChangeQty:Int, val ClOrdID:String, val ContractYear:String,
                             val CustomerOrFirm:Int, val DummyTimeVariable:String, val ErrorMessage:String,
                             val ExceuteQty:Int, val ExchangeMessage:String, val ExchangeOderID:String,
                             val ExchangeTradingID:String, val ExchangeTransactTime:String, val Exchange_Name:Int,
                             val ExternalAlgoActionID:Int, val ExternalAlgoAgentName:String,
                             val ExternalEngineName:String, val FilledQty:Int, val Flag:Boolean,
                             val GTDExpiryDate:String, val HandInt:Int, val IDSource:Int,
                             val IsExternalAlgoOrder:Boolean, val IsSemiManualOrder:Boolean,
                             val IsSquareOffAlgoOrder:Boolean, val LastModifiedTime:String, val LeftQty:Int,
                             val Market_Type:Int, val MaturityDay:Int, val MessageType:String,
                             val OrderDayType:Int, val OrderQty:Int, val OrderStatus:String, val OrderTime:String,
                             val Order_Type:Int, val OrginatorClOrdID:String, val Price:Int, val PriceSend:Int,
                             val QTOrderID:String, val QTOrderValue:String, val QTOrderValue_d:String, val QTTradeID:Int,
                             val RequestId:String, val SHFECancelFlag:Int, val SHFEReplacePrice:String,
                             val SecurityID:String, val SecurityType:String, val SenderComID:String, val ShanghaiOrdIND:String,
                             val ShanghaiOrdValue:String, val StatusBit:Int, val StopPrice:Int,
                             val Symbol_Name:String, val TargetCompId:String, val TickSize:String, val TmpExceuteQty:Int,
                             val TmpOrderQty:Int, val TotalModification:Int, val TrigPrice:Int,
                             val UserID:Int, val UserName:String, val WeightedAvgPrice:Int, val WorkQty:Int,
                             val algoLotChangeFactor:Int, val algoToCancel:Boolean, var LTP: String) : Parcelable



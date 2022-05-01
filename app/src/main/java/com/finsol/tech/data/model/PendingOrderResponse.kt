package com.finsol.tech.data.model

data class PendingOrderResponse(val pendingOrdersList : List<PendingOrderModel>)

data class PendingOrderModel(val accountID: Int, val accountName:String , val algoIdentifierKey:String, val algoName: String,
                             val algoSide:Int, val averagePrice:Int, val beginString:String , val ctclid:String ,
                             val cTCLName:String, val changeQty:Int, val clOrdID:String, val contractYear:String,
                             val customerOrFirm:Int, val dummyTimeVariable:String, val errorMessage:String,
                             val exceuteQty:Int, val exchangeMessage:String, val exchangeOderID:String,
                             val exchangeTradingID:String, val exchangeTransactTime:String, val exchangeName:Int,
                             val externalAlgoActionID:Int, val externalAlgoAgentName:String,
                             val externalEngineName:String, val filledQty:Int, val flag:Boolean,
                             val gTDExpiryDate:String, val handInt:Int, val iDSource:Int,
                             val isExternalAlgoOrder:Boolean, val isSemiManualOrder:Boolean,
                             val isSquareOffAlgoOrder:Boolean, val lastModifiedTime:String, val leftQty:Int,
                             val marketType:Int, val maturityDay:Int, val messageType:String,
                             val orderDayType:Int, val orderQty:Int, val orderStatus:String, val orderTime:String,
                             val orderType:Int, val orginatorClOrdID:String, val price:Int, val priceSend:Int,
                             val qTOrderID:String, val qTOrderValue:String, val qTOrderValueD:String, val qTTradeID:Int,
                             val requestId:String, val sHFECancelFlag:Int, val sHFEReplacePrice:String,
                             val securityID:String, val securityType:String, val senderComID:String,val shanghaiOrdIND:String,
                             val shanghaiOrdValue:String, val statusBit:Int, val stopPrice:Int,
                             val symbolName:String, val targetCompId:String, val tickSize:String, val tmpExceuteQty:Int,
                             val tmpOrderQty:Int, val totalModification:Int, val trigPrice:Int,
                             val userID:Int, val userName:String, val weightedAvgPrice:Int, val workQty:Int,
                             val algoLotChangeFactor:Int, val algoToCancel:Boolean)



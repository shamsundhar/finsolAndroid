package com.finsol.tech.data.model

data class GetAllContractsResponse(val allContracts:List<Contract>, val watchList1: List<Contract>, val watchList2: List<Contract>, val watchList3: List<Contract>)

data class Contract(val closePrice:Integer, val displayName: String, val exchangeName: String, val expiry: String, val ltp: String, val lotSize: String, val maturityDay: String, val securityID: String, val securityType: String, val symbolName: String, val tickSize: String)
//Integer closePrice, String displayName, String exchangeName, String expiry, Integer ltp, Integer lotSize, Integer maturityDay, String securityID, String securityType, String symbolName, Float tickSize
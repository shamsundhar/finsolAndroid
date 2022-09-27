package com.finsol.tech.domain.ctcl

data class CTCLDetails(
    val CTCLName : String = "",
    val ExchangeName : String= "",
    val OpenClientBalance : String= "",
    val UtilisedMargin : String= "",
    val AvailableMargin : String= "",
    val TotalIntradayPnL : String= "",
    val TotalPnL : String= "",
    val ErrorMessage : String = ""

)

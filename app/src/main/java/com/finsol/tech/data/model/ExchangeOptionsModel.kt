package com.finsol.tech.data.model

data class ExchangeOptionsModel(val ExchangeName:String,
                                val OrderTypes:Array<String>,
                                val TimeInForces:Array<String>)

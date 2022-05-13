package com.finsol.tech

import androidx.multidex.MultiDexApplication
import com.finsol.tech.data.model.ExchangeEnumModel
import com.finsol.tech.data.model.GetAllContractsResponse
import dagger.hilt.android.HiltAndroidApp
@HiltAndroidApp
class FinsolApplication: MultiDexApplication() {
    private lateinit var allContractsResponse: GetAllContractsResponse
//    private lateinit var exchangeEnumList: List<ExchangeEnumModel>

    fun getAllContracts(): GetAllContractsResponse {
        return allContractsResponse
    }
    fun setAllContracts(contractsResponse: GetAllContractsResponse) {
        allContractsResponse = contractsResponse
    }
//    fun getExchangeEnumList(): List<ExchangeEnumModel> {
//        return exchangeEnumList
//    }
//    fun setExchangeEnumList(list: List<ExchangeEnumModel>) {
//        this.exchangeEnumList = list
//    }

}
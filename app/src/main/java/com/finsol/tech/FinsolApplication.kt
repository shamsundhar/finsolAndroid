package com.finsol.tech

import androidx.multidex.MultiDexApplication
import com.finsol.tech.data.model.ExchangeEnumModel
import com.finsol.tech.data.model.ExchangeOptionsModel
import com.finsol.tech.data.model.GetAllContractsResponse
import dagger.hilt.android.HiltAndroidApp
@HiltAndroidApp
class FinsolApplication: MultiDexApplication() {
    private lateinit var allContractsResponse: GetAllContractsResponse
    private lateinit var exchangeOptions: Array<ExchangeOptionsModel>

    fun getAllContracts(): GetAllContractsResponse? {
        return if(::allContractsResponse.isInitialized){
            allContractsResponse
        }else{
            null
        }
    }
    fun setAllContracts(contractsResponse: GetAllContractsResponse) {
        allContractsResponse = contractsResponse
    }
    fun getExchangeOptions(): Array<ExchangeOptionsModel> {
        return exchangeOptions
    }
    fun setExchangeOptions(array: Array<ExchangeOptionsModel>) {
        this.exchangeOptions = array
    }

}
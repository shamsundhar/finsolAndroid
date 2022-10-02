package com.finsol.tech

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.finsol.tech.data.model.ExchangeEnumModel
import com.finsol.tech.data.model.ExchangeOptionsModel
import com.finsol.tech.data.model.GetAllContractsResponse
import com.finsol.tech.rabbitmq.RabbitMQ
import com.finsol.tech.util.AppConstants.KEY_PREF_IP_ADDRESS_VALUE
import com.finsol.tech.util.PreferenceHelper
import dagger.hilt.android.HiltAndroidApp
@HiltAndroidApp
class FinsolApplication: MultiDexApplication() {

    companion object {
        lateinit var context: Context
        private lateinit var preferenceHelper: PreferenceHelper
    }

    init {
        context = this
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()
    }

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

    override fun onCreate() {
        super.onCreate()
        val ipAddress = preferenceHelper.getString(
            context,
            KEY_PREF_IP_ADDRESS_VALUE,
            ""
        )
        if(!ipAddress.equals("")){
            RabbitMQ
        }
    }

}
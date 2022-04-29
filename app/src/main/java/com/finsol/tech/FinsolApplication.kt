package com.finsol.tech

import androidx.multidex.MultiDexApplication
import com.finsol.tech.data.model.GetAllContractsResponse
import dagger.hilt.android.HiltAndroidApp
@HiltAndroidApp
class FinsolApplication: MultiDexApplication() {
    private lateinit var allContractsResponse: GetAllContractsResponse

    fun getAllContracts(): GetAllContractsResponse {
        return allContractsResponse
    }
    fun setAllContracts(contractsResponse: GetAllContractsResponse) {
        allContractsResponse = contractsResponse
    }

}
package com.finsol.tech.rabbitmq

import androidx.lifecycle.*
import com.finsol.tech.SingletonNameViewModelFactory
import com.finsol.tech.data.model.Market


object MySingletonViewModel : ViewModel() {

    private var mySingletonViewModel: MySingletonViewModel? = null
    private var mutableLiveData: MutableLiveData<HashMap<String, Market>>? = null

    fun getMyViewModel(owner: ViewModelStoreOwner): MySingletonViewModel {
        initializeMarketHMData()
        if(mySingletonViewModel == null){
            val singletonNameViewModelFactory = SingletonNameViewModelFactory(MySingletonViewModel)
            mySingletonViewModel = ViewModelProvider(owner, singletonNameViewModelFactory).get(MySingletonViewModel::class.java)
        }
        return mySingletonViewModel!!
    }

    fun getContracts(): MutableLiveData<HashMap<String,Market>>? {
        initializeMarketHMData()
        return mutableLiveData
    }

    fun initializeMarketHMData(){
        if (mutableLiveData == null) {
            mutableLiveData = MutableLiveData()
            mutableLiveData?.value = hashMapOf()
        }
    }

    fun updateContract( market : Market){
        getContracts()
        mutableLiveData?.value?.set(market.securityID,market)
        mutableLiveData?.postValue(mutableLiveData?.value)
    }
}
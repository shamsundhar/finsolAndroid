package com.finsol.tech.rabbitmq

import androidx.lifecycle.*
import com.finsol.tech.SingletonNameViewModelFactory
import com.finsol.tech.data.model.Market
import com.finsol.tech.data.model.PendingOrderModel


object MySingletonViewModel : ViewModel() {

    private var mySingletonViewModel: MySingletonViewModel? = null
    private var mutableLiveData: MutableLiveData<HashMap<String, Market>>? = null
    private var userOrdersData: MutableLiveData<PendingOrderModel> = MutableLiveData()

    fun getMyViewModel(owner: ViewModelStoreOwner): MySingletonViewModel {
        initializeMarketHMData()
        if(mySingletonViewModel == null){
            val singletonNameViewModelFactory = SingletonNameViewModelFactory(MySingletonViewModel)
            mySingletonViewModel = ViewModelProvider(owner, singletonNameViewModelFactory).get(MySingletonViewModel::class.java)
        }
        return mySingletonViewModel!!
    }

    fun getMarketData(): MutableLiveData<HashMap<String,Market>>? {
        initializeMarketHMData()
        return mutableLiveData
    }

    fun getUserOrders() : MutableLiveData<PendingOrderModel> = userOrdersData

    fun initializeMarketHMData(){
        if (mutableLiveData == null) {
            mutableLiveData = MutableLiveData()
            mutableLiveData?.value = hashMapOf()
        }
    }

    fun updateContract( market : Market){
        getMarketData()
        mutableLiveData?.value?.set(market.securityID,market)
        mutableLiveData?.postValue(mutableLiveData?.value)
    }

    fun updateUserOrdersData(pendingOrderModel: PendingOrderModel) {
        userOrdersData.postValue(pendingOrderModel)
    }

}
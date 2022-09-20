package com.finsol.tech.rabbitmq

import androidx.lifecycle.*
import com.finsol.tech.FinsolApplication
import com.finsol.tech.R
import com.finsol.tech.SingletonNameViewModelFactory
import com.finsol.tech.data.model.Market
import com.finsol.tech.data.model.PendingOrderModel


object MySingletonViewModel : ViewModel() {

    private var mySingletonViewModel: MySingletonViewModel? = null
    private var mutableLiveData: MutableLiveData<HashMap<String, Market>>? = null
    private var userOrdersData: MutableLiveData<PendingOrderModel> = MutableLiveData()
    private var notificationsTracker: MutableLiveData<HashMap<String, Boolean>> = MutableLiveData(hashMapOf())

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

    fun getNotificationTracker() : MutableLiveData<HashMap<String,Boolean>> = notificationsTracker

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
        updateNotificationTrackerData(null,pendingOrderModel)
    }

    fun updateNotificationTrackerData(
        currentSelectedTab: String?,
        pendingOrderModel: PendingOrderModel?
    ) {
        if(currentSelectedTab!= null){
            notificationsTracker.value?.remove(currentSelectedTab)
        }else {
            if(pendingOrderModel?.OrderStatus.equals("Working",true) ||
                pendingOrderModel?.OrderStatus.equals("Pending",true) ||
                pendingOrderModel?.OrderStatus.equals("Replaced",true)){
                notificationsTracker.value?.put(FinsolApplication.context.getString(R.string.text_pending_orders),true)
            }
            if(pendingOrderModel?.OrderStatus.equals("Filled", true)){
                notificationsTracker.value?.put(FinsolApplication.context.getString(R.string.text_order_history),true)
            }
            if(pendingOrderModel?.OrderStatus.equals("canceled",true)|| pendingOrderModel?.OrderStatus.equals("Rejected",true)){
                notificationsTracker.value?.put(FinsolApplication.context.getString(R.string.order_nbook),true)
            }
        }
        notificationsTracker.postValue(notificationsTracker.value)
    }

}
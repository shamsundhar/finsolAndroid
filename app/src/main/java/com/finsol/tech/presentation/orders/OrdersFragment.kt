package com.finsol.tech.presentation.orders

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.finsol.tech.R
import com.finsol.tech.data.model.OrderHistoryModel
import com.finsol.tech.data.model.PendingOrderModel
import com.finsol.tech.databinding.FragmentOrdersBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.orders.adapter.OrdersHistoryAdapter
import com.finsol.tech.presentation.orders.adapter.OrdersPendingAdapter
import com.finsol.tech.util.AppConstants
import com.finsol.tech.util.AppConstants.KEY_PREF_EXCHANGE_MAP
import com.finsol.tech.util.PreferenceHelper
import com.finsol.tech.util.Utilities
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class OrdersFragment: BaseFragment(){
    private lateinit var ordersViewModel: OrdersViewModel
    private lateinit var binding: FragmentOrdersBinding
    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var progressDialog: ProgressDialog

    private var isObserversInitialized : Boolean = false
    private lateinit var pendingOrdersAdapter: OrdersPendingAdapter
    private lateinit var ordersHistoryAdapter: OrdersHistoryAdapter

    private lateinit var orderHistoryList: List<OrderHistoryModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        binding.toolbar.title.visibility = View.VISIBLE
        binding.toolbar.subTitle.visibility = View.VISIBLE
        binding.toolbar.profilePic.visibility = View.VISIBLE
        progressDialog = ProgressDialog(
            context,
            R.style.AppTheme_Dark_Dialog
        )
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(getString(R.string.text_please_wait))

        ordersViewModel = ViewModelProvider(requireActivity()).get(OrdersViewModel::class.java)
        pendingOrdersClicked()
        binding.toolbar.subTitle.text = preferenceHelper.getString(context, AppConstants.KEY_PREF_NAME, "")

        binding.returnToWatchlist.setOnClickListener{
            findNavController().navigate(R.id.watchListFragment)
        }

        binding.radioGroupOrders.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = group.findViewById<View>(checkedId) as RadioButton
            val isChecked = checkedRadioButton.isChecked
            if (isChecked) {
                if(checkedRadioButton.text.equals("Pending Orders")){
                   pendingOrdersClicked()
                } else {
                   orderHistoryClicked()
                }
            }
        }

        //Pending Order recycler view
        // this creates a vertical layout Manager
        binding.pendingRecyclerView.layoutManager = LinearLayoutManager(context)
        // This will pass the ArrayList to our Adapter
        pendingOrdersAdapter = OrdersPendingAdapter(resources)
        pendingOrdersAdapter.setOnItemClickListener(object: OrdersPendingAdapter.ClickListener {
            override fun onItemClick(model: PendingOrderModel) {
                val bundle = Bundle()
//                bundle.putParcelable("selectedModel", model)
                findNavController().navigate(R.id.to_orderPendingDetailsFragment, bundle)
            }
        })
        // Setting the Adapter with the recyclerview
        binding.pendingRecyclerView.adapter = pendingOrdersAdapter

        //History recycler view
        // this creates a vertical layout Manager
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(context)
        ordersHistoryAdapter = OrdersHistoryAdapter(resources)
        ordersHistoryAdapter.setOnItemClickListener(object: OrdersHistoryAdapter.ClickListener {
            override fun onItemClick(model: OrderHistoryModel) {
                val bundle = Bundle()
                bundle.putParcelable("selectedModel", model)
                groupTrades(model.ExchangeOderID, orderHistoryList)
                bundle.putString("OrderHistoryAP",calculateOrderHistoryAveragePrice(groupTrades(model.ExchangeOderID, orderHistoryList)))
                bundle.putString("OrderHistoryFQ",calculateOrderHistoryFilledQuantity(groupTrades(model.ExchangeOderID, orderHistoryList)).toString())
                findNavController().navigate(R.id.to_orderHistoryDetailsFragment, bundle)
            }
        })

        // Setting the Adapter with the recyclerview
        binding.historyRecyclerView.adapter = ordersHistoryAdapter


        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers();
    }
    private fun groupTrades(exchangeOderID: String, orderHistoryList: List<OrderHistoryModel>):List<OrderHistoryModel> {
        val list = mutableListOf<OrderHistoryModel>()
        orderHistoryList.map {
            if(exchangeOderID == it.ExchangeOderID) {
                list.add(it)
            }
        }
        return list
    }
    fun calculateOrderHistoryAveragePrice(orderHistoryList: List<OrderHistoryModel?>?): String? {
//        avg price = Sum(OrderQty * Price) / Sum(OrderQty)
        var a: Int? = null
        orderHistoryList?.map {
           val b = it?.OrderQty?.times(it.Price)
            a = b?.let { it1 -> a?.plus(it1) }
        }
        val c:Int? = calculateOrderHistoryFilledQuantity(orderHistoryList)

            return (a?.div(c!!)).toString()

    }
    fun calculateOrderHistoryFilledQuantity(orderHistoryList: List<OrderHistoryModel?>?): Int? {
//        filled quantity = Sum(OrderQty)
        var a: Int? = null
        orderHistoryList?.map {
            val b = it?.OrderQty
            a = b?.let { it1 -> a?.plus(it1) }
        }
        return a
    }
    private fun initObservers() {
        if(isObserversInitialized){
            return
        }
        isObserversInitialized = true
        ordersViewModel.mState
            .flowWithLifecycle(lifecycle,  Lifecycle.State.STARTED)
            .onEach {
                    it -> processResponse(it)
            }
            .launchIn(lifecycleScope)
    }
    private fun orderHistoryClicked() {
        ordersViewModel.requestOrderHistoryDetails(preferenceHelper.getString(context, AppConstants.KEY_PREF_USER_ID, ""))
    }
    private fun pendingOrdersClicked() {
        ordersViewModel.requestPendingOrderDetails(preferenceHelper.getString(context, AppConstants.KEY_PREF_USER_ID, ""))
    }
    private fun processResponse(state: OrdersViewState) {
        when(state){
            is OrdersViewState.PendingOrdersSuccessResponse -> handlePendingOrdersSuccessResponse(state.pendingOrdersArray)
            is OrdersViewState.OrderHistorySuccessResponse -> handleOrderHistorySuccessResponse(state.orderHistoryArray)
            is OrdersViewState.IsLoading -> handleLoading(state.isLoading)
        }
    }
    private fun handlePendingOrdersSuccessResponse(pendingOrdersArray: Array<PendingOrderModel>) {
       if(pendingOrdersArray.isEmpty()){
            binding.noOrdersSection.visibility = View.VISIBLE
        } else {
            binding.noOrdersSection.visibility = View.GONE
            binding.pendingOrdersSection.visibility = View.VISIBLE
            binding.ordersHistorySection.visibility = View.GONE
            pendingOrdersAdapter.exchangeMap(preferenceHelper.loadMap(context, KEY_PREF_EXCHANGE_MAP))
            pendingOrdersAdapter.updateList(pendingOrdersArray.toList())
        }
    }
    private fun handleOrderHistorySuccessResponse(orderHistoryArray: Array<OrderHistoryModel>) {
        if(orderHistoryArray.isEmpty()) {
            binding.noOrdersSection.visibility = View.VISIBLE
        } else {
            binding.noOrdersSection.visibility = View.GONE
            binding.pendingOrdersSection.visibility = View.GONE
            binding.ordersHistorySection.visibility = View.VISIBLE
            orderHistoryList = orderHistoryArray.toList()
            ordersHistoryAdapter.updateList(orderHistoryList)
        }
    }
    private fun handleLoading(isLoading: Boolean) {
        if(isLoading){
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }
}
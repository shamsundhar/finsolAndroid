package com.finsol.tech.presentation.orders

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.RadioButton
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.finsol.tech.FinsolApplication
import com.finsol.tech.R
import com.finsol.tech.data.model.*
import com.finsol.tech.databinding.FragmentOrdersBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.buysell.BuySellViewState
import com.finsol.tech.presentation.orders.adapter.OrdersHistoryAdapter
import com.finsol.tech.presentation.orders.adapter.OrdersPendingAdapter
import com.finsol.tech.rabbitmq.MySingletonViewModel
import com.finsol.tech.rabbitmq.RabbitMQ
import com.finsol.tech.util.AppConstants
import com.finsol.tech.util.AppConstants.KEY_PREF_EXCHANGE_MAP
import com.finsol.tech.util.PreferenceHelper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*


class OrdersFragment : BaseFragment() {
    private lateinit var ordersViewModel: OrdersViewModel
    private lateinit var mySingletonViewModel: MySingletonViewModel
    private lateinit var binding: FragmentOrdersBinding
    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var progressDialog: ProgressDialog

    private var isObserversInitialized: Boolean = false
    private lateinit var allContractsResponse: GetAllContractsResponse
    private lateinit var pendingOrdersAdapter: OrdersPendingAdapter
    private lateinit var ordersHistoryAdapter: OrdersHistoryAdapter

    private lateinit var orderHistoryList: List<OrderHistoryModel>
    private lateinit var pendingOrdersList: MutableList<PendingOrderModel>
    var ordersSelected = "Pending Orders"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    //navigate to watchlist fragment.
                    findNavController().navigate(R.id.watchListFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

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
        progressDialog = ProgressDialog(
            context,
            R.style.AppTheme_Dark_Dialog
        )
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(getString(R.string.text_please_wait))

        ordersViewModel = ViewModelProvider(requireActivity()).get(OrdersViewModel::class.java)
        mySingletonViewModel = MySingletonViewModel.getMyViewModel(this)

        mySingletonViewModel.getUserOrders().observe(viewLifecycleOwner) {
            updatePendingOrderData(it)
        }

        mySingletonViewModel.getMarketData()?.observe(viewLifecycleOwner) {
            updateListWithMarketData(it)
        }

        pendingOrdersClicked()
        binding.toolbar.subTitle.text =
            preferenceHelper.getString(context, AppConstants.KEY_PREF_NAME, "")

        binding.returnToWatchlist.setOnClickListener {
            findNavController().navigate(R.id.watchListFragment)
        }

        binding.radioGroupOrders.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = group.findViewById<View>(checkedId) as RadioButton
            val isChecked = checkedRadioButton.isChecked
            if (isChecked) {
                ordersSelected = checkedRadioButton.text.toString()
                if (checkedRadioButton.text.equals("Pending Orders")) {
                    pendingOrdersClicked()
                } else {
                    orderHistoryClicked()
                }
            }
        }

        binding.searchETNew.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                return@OnEditorActionListener true
            }
            false
        })

        binding.searchETNew.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.isNotEmpty()) filter(s.toString())
            }
        })

        //Pending Order recycler view
        // this creates a vertical layout Manager
        binding.pendingRecyclerView.layoutManager = LinearLayoutManager(context)
        // This will pass the ArrayList to our Adapter
        pendingOrdersAdapter = OrdersPendingAdapter(resources)
        pendingOrdersAdapter.setOnItemClickListener(object : OrdersPendingAdapter.ClickListener {
            override fun onItemClick(model: PendingOrderModel) {
                val bundle = Bundle()
                bundle.putParcelable("selectedModel", model.toNonNullModel())
                findNavController().navigate(R.id.to_orderPendingDetailsFragment, bundle)
            }
        })
        // Setting the Adapter with the recyclerview
        binding.pendingRecyclerView.adapter = pendingOrdersAdapter

        //History recycler view
        // this creates a vertical layout Manager
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(context)
        ordersHistoryAdapter = OrdersHistoryAdapter(context, resources)
        ordersHistoryAdapter.setOnItemClickListener(object : OrdersHistoryAdapter.ClickListener {
            override fun onItemClick(model: OrderHistoryModel) {
                val modifiedModel = model.toNonNullOrderHistoryModel()
                val bundle = Bundle()
                bundle.putParcelable("selectedModel", modifiedModel)
                bundle.putString(
                    "OrderHistoryAP",
                    calculateOrderHistoryAveragePrice(
                        groupTrades(
                            modifiedModel.ExchangeOderID,
                            orderHistoryList
                        )
                    )
                )
                bundle.putString(
                    "OrderHistoryFQ",
                    calculateOrderHistoryFilledQuantity(
                        groupTrades(
                            modifiedModel.ExchangeOderID,
                            orderHistoryList
                        )
                    ).toString()
                )
//                val action = OrdersFragmentDirections.toOrderHistoryDetailsFragment(modifiedModel)
//                findNavController().navigate(action)

                findNavController().navigate(R.id.to_orderHistoryDetailsFragment, bundle)
            }
        })

        // Setting the Adapter with the recyclerview
        binding.historyRecyclerView.adapter = ordersHistoryAdapter

        val userID = preferenceHelper.getString(context, AppConstants.KEY_PREF_USER_ID, "")
        RabbitMQ.subscribeForUserUpdates(userID)
        return binding.root
    }

    private fun updatePendingOrderData(pendingOrderModel: PendingOrderModel) {
        if (::pendingOrdersList.isInitialized) {
            var indexToReplace: Int = -1
            pendingOrdersList.mapIndexed { index, it ->
                if (it.UniqueEngineOrderID.equals(pendingOrderModel.UniqueEngineOrderID, true)) {
                    indexToReplace = index
                }
            }

            if (pendingOrderModel.OrderStatus.equals(
                    "Working",
                    true
                ) || pendingOrderModel.OrderStatus.equals("Pending", true)
                || pendingOrderModel.OrderStatus.equals("Replaced", true)
            ) {
                if (indexToReplace == -1) {
                    pendingOrdersList.add(pendingOrderModel)
                } else {
                    pendingOrdersList[indexToReplace] = pendingOrderModel
                }
            } else if (pendingOrderModel.OrderStatus.equals(
                    "Rejected",
                    true
                ) || pendingOrderModel.OrderStatus.equals("canceled", true)
                || pendingOrderModel.OrderStatus.equals("Filled", true)
            ) {
                if (indexToReplace != -1) {
                    pendingOrdersList.removeAt(indexToReplace)
                }
            }

            filter(binding.searchETNew.text.toString())
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    private fun updateListWithMarketData(hashMap: HashMap<String, Market>) {
        if (::pendingOrdersList.isInitialized) {
            this.pendingOrdersList?.forEach { pendingOrderModel ->
                val securityID = pendingOrderModel.SecurityID
                val markertData = hashMap[pendingOrderModel.SecurityID]
                if (securityID.equals(markertData?.securityID, true)) {
                    pendingOrderModel.LTP = if (markertData?.LTP.isNullOrBlank()) {
                        "-"
                    } else {
                        markertData?.LTP.toString()
                    }
                }
            }
        }
        if (::orderHistoryList.isInitialized) {
            this.orderHistoryList?.forEach { orderHistoryModel ->
                val securityID = orderHistoryModel.SecurityID
                val markertData = hashMap[orderHistoryModel.SecurityID]
                if (securityID.equals(markertData?.securityID, true)) {
                    orderHistoryModel.LTP = markertData?.LTP ?: ""
                }
            }
        }
        if (ordersSelected == "Pending Orders") {
            if (::pendingOrdersList.isInitialized) {
                this.pendingOrdersList?.let {
                    if (binding.searchETNew.text.isNotBlank()) {
                        filter(binding.searchETNew.text.toString())
                    } else {
                        pendingOrdersAdapter.updateList(pendingOrdersList)
                    }

                }
            }
        } else {
            if (::orderHistoryList.isInitialized) {
                this.orderHistoryList?.let {
                    ordersHistoryAdapter.updateList(orderHistoryList)
                }
            }
        }
    }

    private fun groupTrades(
        exchangeOderID: String,
        orderHistoryList: List<OrderHistoryModel>
    ): List<OrderHistoryModel> {
        val list = mutableListOf<OrderHistoryModel>()
        orderHistoryList.map {
            if (exchangeOderID == it.ExchangeOderID) {
                list.add(it)
            }
        }
        return list
    }

    fun calculateOrderHistoryAveragePrice(orderHistoryList: List<OrderHistoryModel?>?): String? {
        var a: Double? = 0.0

        if (orderHistoryList != null) {
            for (i in orderHistoryList.indices) {
                val b = orderHistoryList[i]?.let { it.OrderQty.times(it.Price) }
                a = b?.let { it1 -> a?.plus(it1) }
            }
        }

        val c: Int? = calculateOrderHistoryFilledQuantity(orderHistoryList)

        return (a?.div(c!!)).toString()

    }

    fun calculateOrderHistoryFilledQuantity(orderHistoryList: List<OrderHistoryModel?>?): Int? {
        var a: Int? = 0
        if (orderHistoryList != null) {
            for (i in orderHistoryList.indices) {
                val b = orderHistoryList[i]?.OrderQty
                a = b?.let { it1 -> a?.plus(it1) }
            }
        }
        return a
    }

    private fun initObservers() {
        if (isObserversInitialized) {
            return
        }
        isObserversInitialized = true
        ordersViewModel.mState
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { it ->
                processResponse(it)
            }
            .launchIn(lifecycleScope)
    }

    private fun orderHistoryClicked() {
        ordersViewModel.requestOrderHistoryDetails(
            preferenceHelper.getString(
                context,
                AppConstants.KEY_PREF_USER_ID,
                ""
            )
        )
    }

    private fun pendingOrdersClicked() {
        ordersViewModel.requestPendingOrderDetails(
            preferenceHelper.getString(
                context,
                AppConstants.KEY_PREF_USER_ID,
                ""
            )
        )
    }

    private fun performSearch() {
        val searchString = binding.searchETNew.text.toString()
        if (searchString.isNotBlank()) {
            filter(searchString)
        }
    }

    private fun filter(text: String) {

        if (text.isEmpty()) {
            pendingOrdersAdapter.updateList(pendingOrdersList)
            return
        }

        val filteredlist: ArrayList<PendingOrderModel> = ArrayList()

        for (item in pendingOrdersList) {
            if (item.Symbol_Name.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault()))
            ) {
                filteredlist.add(item)
            }
        }

        pendingOrdersAdapter.updateList(filteredlist)
        if (filteredlist.isEmpty()) {
            Toast.makeText(context, "No Data Found..", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processResponse(state: OrdersViewState) {
        when (state) {
            is OrdersViewState.PendingOrdersSuccessResponse -> handlePendingOrdersSuccessResponse(
                state.pendingOrdersArray
            )
            is OrdersViewState.OrderHistorySuccessResponse -> handleOrderHistorySuccessResponse(
                state.orderHistoryArray
            )
            is OrdersViewState.ShowToast -> handleToast(state.message)
            is OrdersViewState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    private fun handlePendingOrdersSuccessResponse(pendingOrdersArray: Array<PendingOrderModel>) {
        if (pendingOrdersArray.isEmpty()) {
            binding.noOrdersSection.visibility = View.VISIBLE
            binding.noPendingOrdersTitle.text = "No Pending Orders Yet"
            binding.pendingOrdersSection.visibility = View.GONE
            binding.ordersHistorySection.visibility = View.GONE
        } else {
            binding.noOrdersSection.visibility = View.GONE
            binding.pendingOrdersSection.visibility = View.VISIBLE
            binding.ordersHistorySection.visibility = View.GONE
            pendingOrdersAdapter.exchangeMap(
                preferenceHelper.loadMap(
                    context,
                    KEY_PREF_EXCHANGE_MAP
                )
            )
            pendingOrdersList = pendingOrdersArray.toMutableList()
            //TODO - update ltp value here with all contracts response
            allContractsResponse =
                (requireActivity().application as FinsolApplication).getAllContracts()
            allContractsResponse.allContracts = allContractsResponse.allContracts +
                    allContractsResponse.watchlist1 +
                    allContractsResponse.watchlist2 +
                    allContractsResponse.watchlist3
            pendingOrdersList.forEach { pendingOrderModel ->
                allContractsResponse.allContracts.forEach {
                    if(it.securityID == pendingOrderModel.SecurityID){
                        pendingOrderModel.LTP = it.lTP.toString()
                    }
                }
            }

            if (binding.searchETNew.text.isNotBlank()) {
                filter(binding.searchETNew.text.toString())
            } else {
                pendingOrdersAdapter.updateList(pendingOrdersList)
            }
        }
    }

    private fun handleOrderHistorySuccessResponse(orderHistoryArray: Array<OrderHistoryModel>) {
        if (orderHistoryArray.isEmpty()) {
            binding.noOrdersSection.visibility = View.VISIBLE
            binding.noPendingOrdersTitle.text = "No Order History"
            binding.pendingOrdersSection.visibility = View.GONE
            binding.ordersHistorySection.visibility = View.GONE
        } else {
            binding.noOrdersSection.visibility = View.GONE
            binding.pendingOrdersSection.visibility = View.GONE
            binding.ordersHistorySection.visibility = View.VISIBLE
            orderHistoryList = orderHistoryArray.toList()
            allContractsResponse =
                (requireActivity().application as FinsolApplication).getAllContracts()
            allContractsResponse.allContracts = allContractsResponse.allContracts +
                    allContractsResponse.watchlist1 +
                    allContractsResponse.watchlist2 +
                    allContractsResponse.watchlist3
            orderHistoryList.forEach { orderHistoryModel ->
                allContractsResponse.allContracts.forEach {
                    if(it.securityID == orderHistoryModel.SecurityID){
                        orderHistoryModel.LTP = it.lTP.toString()
                        orderHistoryModel.maturityDay = it.maturityDay
                    }
                }
            }
            ordersHistoryAdapter.updateList(orderHistoryList)
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        if (isLoading) {
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }
}
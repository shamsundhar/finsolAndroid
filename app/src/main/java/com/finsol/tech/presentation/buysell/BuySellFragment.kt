package com.finsol.tech.presentation.buysell

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TableRow
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.finsol.tech.FinsolApplication
import com.finsol.tech.R
import com.finsol.tech.data.model.*
import com.finsol.tech.databinding.FragmentBuySellBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.rabbitmq.MySingletonViewModel
import com.finsol.tech.util.AppConstants.KEY_PREF_DARK_MODE
import com.finsol.tech.util.AppConstants.KEY_PREF_USER_ID
import com.finsol.tech.util.PreferenceHelper
import com.finsol.tech.util.Utilities
import com.ncorti.slidetoact.SlideToActView
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.ArrayList


class BuySellFragment : BaseFragment() {
    private lateinit var mySingletonViewModel: MySingletonViewModel
    private val marketObserver = Observer<HashMap<String, Market>> {processMarketData(it)}
    private lateinit var binding: FragmentBuySellBinding
    private lateinit var buySellViewModel: BuySellViewModel
    private lateinit var preferenceHelper: PreferenceHelper
    private var contractsModel: Contracts? = null
    private var orderHistoryModel: OrderHistoryModel? = null
    private var orderPendingModel: PendingOrderModel? = null
    private var portfolioModel: PortfolioData? = null
    private var populateValidity:Int? = null
    private var populateType:String? = null
    private lateinit var progressDialog: ProgressDialog
    private var isObserversInitialized: Boolean = false
    private var isDarkMode: Boolean = false
    private var mode: String? = ""
    private var fromScreen: String? = ""
    private var userID = ""
    private var securityID = ""
    private var exchangeName = ""
    private var buySelected: Boolean = true
    private lateinit var validityArray: Array<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBuySellBinding.inflate(inflater, container, false)
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()
        buySellViewModel = ViewModelProvider(requireActivity()).get(BuySellViewModel::class.java)

        mode = arguments?.getString("selectedMode")
        fromScreen = arguments?.getString("fromScreen")
        orderHistoryModel = arguments?.getParcelable("selectedOrderHistoryModel")
        orderPendingModel = arguments?.getParcelable("selectedOrderPendingModel")
        contractsModel = arguments?.getParcelable("selectedContractsModel")
        portfolioModel = arguments?.getParcelable("selectedPortfolioModel")
        userID = preferenceHelper.getString(context, KEY_PREF_USER_ID, "")
        if (fromScreen.equals("OrderHistory")) {
            securityID = orderHistoryModel?.SecurityID.toString()
            exchangeName = orderHistoryModel?.exchangeNameString.toString()
            binding.qtyET.setText(orderHistoryModel?.OrderQty.toString())
            binding.priceET.setText(orderHistoryModel?.Price.toString())
            setOrderHistoryData()
        } else if (fromScreen.equals("OrderPending")) {
            securityID = orderPendingModel?.SecurityID.toString()
            exchangeName = orderPendingModel?.exchangeNameString.toString()
            binding.qtyET.setText(orderPendingModel?.OrderQty.toString())
            binding.priceET.setText(orderPendingModel?.PriceSend.toString())
            setOrderPendingData()
        } else if (fromScreen.equals("Portfolio")) {
            securityID = portfolioModel?.securityID.toString()
            exchangeName = portfolioModel?.exchangeNameString.toString()
            binding.qtyET.setText(portfolioModel?.quantity.toString())
            binding.priceET.setText(portfolioModel?.price.toString())
            setPortfolioData()
        } else {
            securityID = contractsModel?.securityID.toString()
            exchangeName = contractsModel?.exchangeName.toString()
            binding.qtyET.setText(contractsModel?.quantity.toString())
            binding.priceET.setText(contractsModel?.price.toString())
            setContractsData()
        }

        mySingletonViewModel = MySingletonViewModel.getMyViewModel(this)
        mySingletonViewModel.getMarketData()?.observe(viewLifecycleOwner) {
            if (fromScreen.equals("OrderHistory")) {
                val marketData = it[orderHistoryModel?.SecurityID]
                marketData?.let {
                    orderHistoryModel?.LTP = marketData.LTP
                    orderHistoryModel?.closePrice = marketData.ClosePrice.toFloat() ?: 0f
                    orderHistoryModel?.updatedTime = Utilities.getCurrentTime()
                    setOrderHistoryData()
                }
            } else if (fromScreen.equals("OrderPending")) {
                val marketData = it[orderPendingModel?.SecurityID]
                marketData?.let {
                    orderPendingModel?.LTP = marketData.LTP
                    orderPendingModel?.closePrice = marketData.ClosePrice.toFloat() ?: 0f
                    orderPendingModel?.updatedTime = Utilities.getCurrentTime()
                    setOrderPendingData()
                }
            } else if (fromScreen.equals("Portfolio")) {
                val marketData = it[portfolioModel?.securityID.toString()]
                marketData?.let {
                    portfolioModel?.LTP = marketData.LTP
                    portfolioModel?.closePrice = marketData.ClosePrice.toFloat() ?: 0f
                    portfolioModel?.updatedTime = Utilities.getCurrentTime()
                    setPortfolioData()
                }
            } else {
                val marketData = it[contractsModel?.securityID]
                marketData?.let {
                    contractsModel?.lTP = marketData.LTP.toDouble()
                    contractsModel?.closePrice = marketData.ClosePrice.toFloat() ?: 0f
                    contractsModel?.updatedTime = Utilities.getCurrentTime()
                    setContractsData()
                }
            }
        }

        progressDialog = ProgressDialog(
            context,
            R.style.AppTheme_Dark_Dialog
        )
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(getString(R.string.text_please_wait))

        setInitialData()
        setValidityAndTypeData()
        applyDarkModeData()
        setClickListeners()

        return binding.root
    }

    private fun processMarketData(it: HashMap<String, Market>?) {
        it?.let {
            if(it.contains(contractsModel?.securityID)){
                val market = it.get(contractsModel?.securityID)
                mySingletonViewModel.getMarketData()?.removeObserver(marketObserver)
                setDefaultValues(market)
            }
        }


    }

    private fun setDefaultValues(market: Market?) {

        var defaults : ArrayList<Float>? = null
        if (mode.equals("Buy") && market?.askPrice?.size!! > 0) {
            defaults = market.askPrice[0]
        }else if(market?.bidPrice?.size!! > 0){
            defaults = market.bidPrice[0]
        }

        binding.qtyET.setText("1")
        binding.priceET.setText(defaults?.get(0).toString())

    }

    private fun initObservers() {
        if (isObserversInitialized) {
            return
        }
        mySingletonViewModel.getMarketData()?.observe(viewLifecycleOwner, marketObserver)
        isObserversInitialized = true
        buySellViewModel.mState
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { it ->
                processResponse(it)
            }
            .launchIn(lifecycleScope)
    }

    private fun processResponse(state: BuySellViewState) {
        when (state) {
            is BuySellViewState.SellSuccessResponse -> handleSellSuccessResponse()
            is BuySellViewState.BuySuccessResponse -> handleBuySuccessResponse()
            is BuySellViewState.modifySuccessResponse -> handleModifySuccessResponse()
            is BuySellViewState.IsLoading -> handleLoading(state.isLoading)
            is BuySellViewState.ShowToast -> handleToast(state.message)
            is BuySellViewState.ErrorResponse -> {
                buySellViewModel.resetStateToDefault()
                binding.confirmButton.resetSlider()
            }
        }
    }

    private fun handleBuySuccessResponse() {
        buySellViewModel.resetStateToDefault()
        Utilities.showDialogWithOneButton(
            context,
            "Buy Order Placed"
        ) {
            findNavController().navigate(R.id.ordersFragment)
        }
    }

    private fun handleSellSuccessResponse() {
        buySellViewModel.resetStateToDefault()
        Utilities.showDialogWithOneButton(
            context,
            "Sell Order Placed"
        ) {
            findNavController().navigate(R.id.ordersFragment)
        }
    }

    private fun handleModifySuccessResponse() {
        buySellViewModel.resetStateToDefault()
        Utilities.showDialogWithOneButton(
            context,
            "Order Modified Successfully"
        ) {
            findNavController().navigate(R.id.ordersFragment)
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        if (isLoading) {
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }

    private fun setOrderHistoryData() {
        binding.toolbar.title2.text = orderHistoryModel?.Symbol_Name
        binding.tickValue.text = "Tick Size: "+orderHistoryModel?.tickSize.toString()
        binding.lotValue.text = "Lot Size: "+orderHistoryModel?.lotSize.toString()
        calcChangePercent(orderHistoryModel?.LTP.toString(), orderHistoryModel?.closePrice)
        binding.symbolTimeText.text =
            if (orderHistoryModel?.updatedTime?.isBlank() == true) "-" else orderHistoryModel?.updatedTime
    }

    private fun setOrderPendingData() {
        binding.toolbar.title2.text = orderPendingModel?.Symbol_Name
        binding.tickValue.text = orderPendingModel?.tickSize.toString()
        binding.lotValue.text = orderPendingModel?.lotSize.toString()
        populateValidity = orderPendingModel?.OrderDayType
        populateType = orderPendingModel?.Market_Type?.let {
            when (it) {
                1 -> "MARKET"
                2 -> "LIMIT"
                3 -> "STOP"
                4 -> "STOPLIMIT"
                5 -> "ICEBERG"
                else -> ""
            }
        }
        if(populateType == "STOPLIMIT" || populateType == "ICEBERG") {
            binding.triggerET.setText(orderPendingModel?.StopPrice.toString())
        } else {
            binding.triggerET.setText("0.0")
        }
        binding.confirmButton.text = "Modify Order"
        calcChangePercent(orderPendingModel?.LTP.toString(), orderPendingModel?.closePrice)
        binding.symbolTimeText.text =
            if (orderPendingModel?.updatedTime?.isBlank() == true) "-" else orderPendingModel?.updatedTime
    }

    private fun setContractsData() {
        binding.toolbar.title2.text = contractsModel?.symbolName
        binding.tickValue.text = contractsModel?.tickSize.toString()
        binding.lotValue.text = contractsModel?.lotSize.toString()
        calcChangePercent(contractsModel?.lTP.toString(), contractsModel?.closePrice)
        binding.symbolTimeText.text =
            if (contractsModel?.updatedTime?.isBlank() == true) "-" else contractsModel?.updatedTime
    }

    private fun setPortfolioData() {
        binding.toolbar.title2.text = portfolioModel?.productSymbol
        binding.tickValue.text = portfolioModel?.tickSize.toString()
        binding.lotValue.text = portfolioModel?.lotSize.toString()
        calcChangePercent(portfolioModel?.LTP.toString(), portfolioModel?.closePrice)
        binding.symbolTimeText.text =
            if (portfolioModel?.updatedTime?.isBlank() == true) "-" else portfolioModel?.updatedTime
    }

    private fun calcChangePercent(ltp: String, closePrice: Float?) {
        binding.symbolPrice.text = ltp.toString()
        if (ltp == "-" || ltp.isNotEmpty()) {
            val change =
                ltp.toDouble().minus(closePrice?.toDouble()!!)
            val changePercent: Float
            if (closePrice.toDouble() != 0.0) {
                changePercent = ((change / closePrice.toDouble()) * 100).toFloat()
            } else {
                changePercent = ((change).times(100)).toFloat()
            }
            if (change != null) {
                if (change >= 0.0) {
                    context?.let {
                        binding.symbolPercentage.setTextColor(
                            ContextCompat.getColor(
                                it,
                                (R.color.green)
                            )
                        )
                    }
                    binding.symbolStatusImage.setImageResource(R.drawable.ic_up_green)
                } else {
                    context?.let {
                        binding.symbolPercentage.setTextColor(
                            ContextCompat.getColor(
                                it,
                                (R.color.red)
                            )
                        )
                    }
                    binding.symbolStatusImage.setImageResource(R.drawable.ic_down_red)
                }
            }
            binding.symbolPercentage.text = java.lang.String.format(
                resources.getString(R.string.text_cumulative_pnl),
                change
            ) + "(" + java.lang.String.format(
                resources.getString(R.string.text_cumulative_pnl),
                changePercent
            ) + "%)"
        } else {
            binding.symbolPercentage.text = "-"
        }
    }


    private fun setClickListeners() {
        binding.radioGroupBuySell.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = group.findViewById<View>(checkedId) as RadioButton
            val isChecked = checkedRadioButton.isChecked
            if (isChecked) {
                if (checkedRadioButton.text.equals("Buy")) {
                    buySelected = true
                    if (!isDarkMode) binding.rootLayout.setBackgroundColor(resources.getColor(R.color.colorSecondary))
                } else {
                    buySelected = false
                    if (!isDarkMode) binding.rootLayout.setBackgroundColor(resources.getColor(R.color.lavender_blush))
                }
            }
        }
        binding.toolbar.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

//        binding.confirmButton.setOnClickListener {
//            if (validate()) {
//                val timeInForce: Int =
//                    validityArray.indexOf(binding.validityTableLayout.checkedRadioButtonText)
//                if (buySelected) {
//                    //TODO call buy api
//                    buySellViewModel.placeBuyOrder(
//                        securityID,
//                        userID,
//                        binding.typeTableLayout.checkedRadioButtonText,
//                        (timeInForce + 1).toString(),
//                        binding.priceET.text.toString().trim(),
//                        binding.qtyET.text.toString().trim()
//                    )
//                } else {
//                    //TODO call sell api
//                    buySellViewModel.placeSellOrder(
//                        securityID,
//                        userID,
//                        binding.typeTableLayout.checkedRadioButtonText,
//                        (timeInForce + 1).toString(),
//                        binding.priceET.text.toString().trim(),
//                        binding.qtyET.text.toString().trim()
//                    )
//                }
//            }
//        }
        binding.confirmButton.onSlideCompleteListener =
            object : SlideToActView.OnSlideCompleteListener {
                override fun onSlideComplete(view: SlideToActView) {
                    if (validate()) {
                        val timeInForce: Int =
                            validityArray.indexOf(binding.validityTableLayout.checkedRadioButtonText)
                        if (fromScreen == "OrderPending") {
                            buySellViewModel.modifyOrder( orderPendingModel?.UniqueEngineOrderID.toString(),
                                binding.triggerET.text.toString().trim(),
                                binding.priceET.text.toString().trim(),
                                binding.qtyET.text.toString().trim())
                        } else {
                        if (buySelected) {
                            buySellViewModel.placeBuyOrder(
                                securityID,
                                userID,
                                binding.typeTableLayout.checkedRadioButtonText,
                                (timeInForce + 1).toString(),
                                binding.priceET.text.toString().trim(),
                                binding.qtyET.text.toString().trim(),
                                binding.triggerET.text.toString().trim()
                            )
                        } else {
                            buySellViewModel.placeSellOrder(
                                securityID,
                                userID,
                                binding.typeTableLayout.checkedRadioButtonText,
                                (timeInForce + 1).toString(),
                                binding.priceET.text.toString().trim(),
                                binding.qtyET.text.toString().trim(),
                                binding.triggerET.text.toString().trim()
                            )
                        }
                    }
                    }else {
                        view.resetSlider()
                    }

                }
            }
    }

    private fun setInitialData() {
        binding.toolbar.title2.visibility = View.VISIBLE
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.marginLayout.visibility = View.GONE
    }

    private fun applyDarkModeData() {
        isDarkMode = preferenceHelper.getBoolean(context, KEY_PREF_DARK_MODE, false)
        if (mode.equals("Buy")) {
            buySelected = true
            binding.radioButtonBuy.isChecked = true
            if(fromScreen == "OrderPending"){
                binding.radioButtonSell.isEnabled = false
            }
            if (!isDarkMode) binding.rootLayout.setBackgroundColor(resources.getColor(R.color.colorSecondary))
        } else {
            buySelected = false
            binding.radioButtonSell.isChecked = true
            if(fromScreen == "OrderPending"){
                binding.radioButtonBuy.isEnabled = false
            }
            if (!isDarkMode) binding.rootLayout.setBackgroundColor(resources.getColor(R.color.lavender_blush))
        }
    }

    private fun rbTypeClicked() {
        if (binding.typeTableLayout.checkedRadioButtonText.equals("Stoplimit") ||
            binding.typeTableLayout.checkedRadioButtonText.equals("Stop")
        ) {
            binding.triggerET.visibility = View.VISIBLE
            binding.triggerTv.visibility = View.VISIBLE
            binding.triggerTv.text = "Trigger"
        } else if(binding.typeTableLayout.checkedRadioButtonText.equals("Iceberg")) {
            binding.triggerET.visibility = View.VISIBLE
            binding.triggerTv.visibility = View.VISIBLE
            binding.triggerTv.text = "Disclose Quantity"
        } else {
            binding.triggerET.visibility = View.GONE
            binding.triggerTv.visibility = View.GONE
        }
        binding.priceET.isEnabled = !binding.typeTableLayout.checkedRadioButtonText.equals("Market")

    }

    private fun validate(): Boolean {
        var result = false
        val price = binding.priceET.text.toString()
        val quantity = binding.qtyET.text.toString()
        val lotSize = binding.lotValue.text.toString()
        val triggerValue = binding.triggerET.text.toString()
        val isTriggerDisplayed = binding.triggerET.visibility == View.VISIBLE
        val tickValue = binding.tickValue.text.toString()
        val validitySelected = binding.validityTableLayout.checkedRadioButtonText.isNotBlank()
        val typeSelected = binding.typeTableLayout.checkedRadioButtonText.isNotBlank()
        if (price.isNotBlank()) {
            binding.priceET.error = null
            if (quantity.isNotBlank()) {
                binding.qtyET.error = null
                if (isTriggerDisplayed && triggerValue.isNotBlank()) {
                    binding.triggerET.error = null
                    if (roundOffDecimal((price.toDouble()) % (tickValue.toDouble())) == 0.0) {
                        if((quantity.toInt()) >= 1) {
                            if (validitySelected) {
                                if (typeSelected) {
                                    result = true
                                } else {
                                    result = false
                                    Utilities.showDialogWithOneButton(
                                        context,
                                        "Select Order Type", null
                                    )
                                }
                            } else {
                                result = false
                                Utilities.showDialogWithOneButton(
                                    context,
                                    "Select Order Validity", null
                                )
                            }
                        } else {
                            result = false
                            Utilities.showDialogWithOneButton(
                                context,
                                "Quantity should be more than 0", null
                            )
                        }
                    } else {
                        result = false
                        Utilities.showDialogWithOneButton(
                            context,
                            "Price should be in multiples of $tickValue", null
                        )
                    }
                } else {
                    if (isTriggerDisplayed) {
                        binding.triggerET.error = "Field should not be empty"
                        Utilities.hideSoftKeyboard(context, binding.triggerET)
                    } else {
                        if (roundOffDecimal((price.toDouble()) % (tickValue.toDouble())) == 0.0) {
                            if((quantity.toInt()) >= 1) {
                                if (validitySelected) {
                                    if (typeSelected) {
                                        result = true
                                    } else {
                                        result = false
                                        Utilities.showDialogWithOneButton(
                                            context,
                                            "Select Order Type", null
                                        )
                                    }
                                } else {
                                    result = false
                                    Utilities.showDialogWithOneButton(
                                        context,
                                        "Select Order Validity", null
                                    )
                                }
                            }
                            else {
                                result = false
                                Utilities.showDialogWithOneButton(
                                    context,
                                    "Quantity should be more than 0", null
                                )
                            }
                        } else {
                            result = false
                            Utilities.showDialogWithOneButton(
                                context,
                                "Price should be in multiples of $tickValue", null
                            )
                        }
                    }
                }
            } else {
                binding.qtyET.error = "Field should not be empty"
                Utilities.hideSoftKeyboard(context, binding.qtyET)
            }
        } else {
            binding.priceET.error = "Field should not be empty"
            Utilities.hideSoftKeyboard(context, binding.priceET)
        }
        return result
    }

    fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.FLOOR
        return df.format(number).toDouble()
    }

    private fun setValidityAndTypeData() {
        val tableRow = TableRow(context)
        tableRow.layoutParams =
            binding.validityTableLayout.layoutParams // TableLayout is the parent view

        val rowParams1 = TableRow.LayoutParams(1)
        val rowParams2 = TableRow.LayoutParams(2)
        val rowParams3 = TableRow.LayoutParams(3)
        val rowParams4 = TableRow.LayoutParams(4)
        rowParams1.setMargins(10, 10, 10, 10)
        rowParams2.setMargins(10, 10, 10, 10)
        rowParams3.setMargins(10, 10, 10, 10)
        rowParams4.setMargins(10, 10, 10, 10)

        val exchangeOptions =
            (requireActivity().application as FinsolApplication).getExchangeOptions()

        for (exchangeOption in exchangeOptions) {
            Log.e("exchange options:name:", exchangeOption.ExchangeName)

            if (exchangeName == exchangeOption.ExchangeName) {
                var typeTableRow = TableRow(context)
                typeTableRow.layoutParams = binding.typeTableLayout.layoutParams
                binding.typeTableLayout.setClickListener { rbTypeClicked() }
                for (typeIndex in exchangeOption.OrderTypes.indices) {
                    Log.e("order type:", exchangeOption.OrderTypes.get(typeIndex) + "")
                    if (typeIndex == 4 || typeIndex == 8) {
                        binding.typeTableLayout.addView(typeTableRow)
                        typeTableRow = TableRow(context)
                        typeTableRow.layoutParams =
                            binding.typeTableLayout.layoutParams // TableLayout is the parent view
                    }
                    val radioButton: RadioButton =
                        layoutInflater.inflate(R.layout.view_radio_button, null) as RadioButton
                    radioButton.text = exchangeOption.OrderTypes[typeIndex]
                    if(fromScreen == "OrderPending") {
                        if(populateType?.lowercase() == exchangeOption.OrderTypes[typeIndex].lowercase()) {
                            binding.typeTableLayout.setActiveRadioButton(radioButton)
                        }
                        else{
                            radioButton.isEnabled = false
                        }
                    } else {
                        if(typeIndex == 0){
                            binding.typeTableLayout.setActiveRadioButton(radioButton)
                        }
                    }
                    radioButton.layoutParams = rowParams1
                    typeTableRow.addView(radioButton)
                }
                binding.typeTableLayout.addView(typeTableRow)

                var tableRow = TableRow(context)
                tableRow.layoutParams = binding.validityTableLayout.layoutParams
                validityArray = exchangeOption.TimeInForces
                for (itemIndex in exchangeOption.TimeInForces.indices) {
                    Log.e("time in forces:", "" + exchangeOption.TimeInForces[itemIndex])
                    if (itemIndex == 4 || itemIndex == 8) {
                        binding.validityTableLayout.addView(tableRow)
                        tableRow = TableRow(context)
                        tableRow.layoutParams =
                            binding.validityTableLayout.layoutParams // TableLayout is the parent view
                    }
                    val radioButton: RadioButton =
                        layoutInflater.inflate(R.layout.view_radio_button, null) as RadioButton
                    radioButton.text = exchangeOption.TimeInForces[itemIndex]
                    if(fromScreen == "OrderPending") {
                        if(populateValidity == (itemIndex+1)) {
                            binding.validityTableLayout.setActiveRadioButton(radioButton)
                        } else{
                            radioButton.isEnabled = false
                        }
                    } else {
                        if(itemIndex == 0){
                            binding.validityTableLayout.setActiveRadioButton(radioButton)
                        }
                    }
                    when (itemIndex) {
                        0 -> radioButton.layoutParams = rowParams1
                        1 -> radioButton.layoutParams = rowParams2
                        2 -> radioButton.layoutParams = rowParams3
                        3 -> radioButton.layoutParams = rowParams4
                        4 -> radioButton.layoutParams = rowParams1
                        5 -> radioButton.layoutParams = rowParams2
                        6 -> radioButton.layoutParams = rowParams3
                        7 -> radioButton.layoutParams = rowParams4
                    }
                    // TableRow is the parent view
                    tableRow.addView(radioButton)
                }
                binding.validityTableLayout.addView(tableRow)
//                binding.validityTableLayout.setClickListener { rbValidityClicked() }
            }

        }
    }

}
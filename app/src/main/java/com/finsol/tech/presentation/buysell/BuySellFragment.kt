package com.finsol.tech.presentation.buysell

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TableRow
import android.widget.Toast
import androidx.constraintlayout.motion.widget.TransitionBuilder.validate
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.finsol.tech.FinsolApplication
import com.finsol.tech.R
import com.finsol.tech.data.model.Contracts
import com.finsol.tech.data.model.OrderHistoryModel
import com.finsol.tech.databinding.FragmentBuySellBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.orders.OrdersViewModel
import com.finsol.tech.presentation.orders.OrdersViewState
import com.finsol.tech.presentation.watchlist.adapter.ChildWatchListAdapter1
import com.finsol.tech.rabbitmq.MySingletonViewModel
import com.finsol.tech.util.AppConstants.KEY_PREF_DARK_MODE
import com.finsol.tech.util.AppConstants.KEY_PREF_USER_ID
import com.finsol.tech.util.PreferenceHelper
import com.finsol.tech.util.ToggleButtonGroupTableLayout
import com.finsol.tech.util.Utilities
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class BuySellFragment: BaseFragment() {
    private lateinit var mySingletonViewModel: MySingletonViewModel
    private lateinit var binding: FragmentBuySellBinding
    private lateinit var buySellViewModel: BuySellViewModel
    private lateinit var preferenceHelper: PreferenceHelper
    private var contractsModel:Contracts? = null
    private var orderHistoryModel:OrderHistoryModel? = null
    private lateinit var progressDialog: ProgressDialog
    private var isObserversInitialized : Boolean = false
    private var isDarkMode: Boolean = false
    private var mode: String? = ""
    private var userID = ""
    private var securityID = ""
    private var buySelected:Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers();
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
        orderHistoryModel = arguments?.getParcelable("selectedModel")
        contractsModel = arguments?.getParcelable("selectedContractsModel")
        userID = preferenceHelper.getString(context, KEY_PREF_USER_ID, "")
        securityID = contractsModel?.securityID.toString()
        mySingletonViewModel = MySingletonViewModel.getMyViewModel(this)

        mySingletonViewModel.getMarketData()?.observe(viewLifecycleOwner){
            val marketData = it[contractsModel?.securityID]
            marketData?.let {
                contractsModel?.lTP = marketData.LTP.toDouble()
                contractsModel?.updatedTime = Utilities.getCurrentTime()
                setContractsData()
            }
        }

        progressDialog = ProgressDialog(
            context,
            R.style.AppTheme_Dark_Dialog
        )
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(getString(R.string.text_please_wait))

        //TODO display symbol price from Market Data
//        binding.symbolPrice.text = ""
        setInitialData()
        setOrderHistoryData()
        setContractsData()
        setValidityAndTypeData()
        applyDarkModeData()
        setClickListeners()

        return binding.root
    }

    private fun initObservers() {
        if(isObserversInitialized){
            return
        }
        isObserversInitialized = true
        buySellViewModel.mState
            .flowWithLifecycle(lifecycle,  Lifecycle.State.STARTED)
            .onEach {
                    it -> processResponse(it)
            }
            .launchIn(lifecycleScope)
    }
    private fun processResponse(state: BuySellViewState) {
        when(state){
            is BuySellViewState.SellSuccessResponse -> handleSellSuccessResponse()
            is BuySellViewState.BuySuccessResponse -> handleBuySuccessResponse()
            is BuySellViewState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    private fun handleBuySuccessResponse() {
        findNavController().navigate(R.id.ordersFragment)
    }

    private fun handleSellSuccessResponse() {
        findNavController().navigate(R.id.ordersFragment)
    }

    private fun handleLoading(isLoading: Boolean) {
        if(isLoading){
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }
    private fun setOrderHistoryData() {
//        binding.tickValue.text = orderHistoryModel.
    }
    private fun setContractsData() {
        binding.tickValue.text = contractsModel?.tickSize.toString()
        binding.lotValue.text = contractsModel?.lotSize.toString()
        binding.symbolPrice.text = contractsModel?.lTP.toString()
        val change = contractsModel?.lTP?.minus(contractsModel?.closePrice!!)
        val changePercent:Float
        if(contractsModel?.closePrice != 0f){
            if (change != null) {
                changePercent = ((change/contractsModel?.closePrice!!)*100).toFloat()
            } else{
                changePercent = 0.0F
            }
        }
        else {
            changePercent = ((change)?.times(100))?.toFloat()!!
        }
        binding.symbolPercentage.text = changePercent.toString()+"%"
        binding.symbolTimeText.text = contractsModel?.updatedTime

    }

    private fun setClickListeners() {
        binding.radioGroupBuySell.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = group.findViewById<View>(checkedId) as RadioButton
            val isChecked = checkedRadioButton.isChecked
            if (isChecked) {
                if(checkedRadioButton.text.equals("Buy")){
                    buySelected = true
                    if(!isDarkMode)binding.rootLayout.setBackgroundColor(resources.getColor(R.color.colorSecondary))
                } else {
                    buySelected = false
                    if(!isDarkMode)binding.rootLayout.setBackgroundColor(resources.getColor(R.color.lavender_blush))
                }
            }
        }
        binding.toolbar.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.confirmButton.setOnClickListener {
            if(validate()){
                if(buySelected) {
                    //TODO call buy api
                    buySellViewModel.placeBuyOrder(securityID,
                        userID,
                        binding.typeTableLayout.checkedRadioButtonText,
                        "1",
                        binding.priceET.text.toString().trim(),
                        binding.qtyET.text.toString().trim())
                } else {
                    //TODO call sell api
                    buySellViewModel.placeSellOrder(securityID,
                        userID,
                        binding.typeTableLayout.checkedRadioButtonText,
                        "1",
                        binding.priceET.text.toString().trim(),
                        binding.qtyET.text.toString().trim())
                }
            }
        }
    }

    private fun setInitialData() {
        binding.toolbar.title2.visibility = View.VISIBLE
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.marginLayout.visibility = View.GONE
    }
    private fun applyDarkModeData(){
        isDarkMode = preferenceHelper.getBoolean(context, KEY_PREF_DARK_MODE, false)
        if(mode.equals("Buy")){
            binding.radioButtonBuy.isChecked = true
            if(!isDarkMode)binding.rootLayout.setBackgroundColor(resources.getColor(R.color.colorSecondary))
        } else{
            binding.radioButtonSell.isChecked = true
            if(!isDarkMode)binding.rootLayout.setBackgroundColor(resources.getColor(R.color.lavender_blush))
        }
    }
    private fun rbTypeClicked() {
        if(binding.typeTableLayout.checkedRadioButtonText.equals("Stoplimit") ||
            binding.typeTableLayout.checkedRadioButtonText.equals("Stop")) {
            binding.triggerET.visibility = View.VISIBLE
            binding.triggerTv.visibility = View.VISIBLE
        } else {
            binding.triggerET.visibility = View.GONE
            binding.triggerTv.visibility = View.GONE
        }
        binding.priceET.isEnabled = !binding.typeTableLayout.checkedRadioButtonText.equals("Market")

    }

    private fun validate():Boolean {
        var result = false
        val price = binding.priceET.text.toString()
        val quantity = binding.qtyET.text.toString()
        val tickValue = binding.tickValue.text.toString()
        val validitySelected = binding.validityTableLayout.checkedRadioButtonText.isNotBlank()
        val typeSelected = binding.typeTableLayout.checkedRadioButtonText.isNotBlank()
        if(price.isNotBlank()){
            binding.priceET.error = null
            if(quantity.isNotBlank()){
                binding.qtyET.error = null
                if((price.toDouble())%(tickValue.toDouble()) == 0.0){
                    if(validitySelected) {
                        if(typeSelected) {
                            result = true
                        } else {
                            result = false
                            Utilities.showDialogWithOneButton(context,
                                "Select Order Type", null)
                        }
                    } else {
                        result = false
                        Utilities.showDialogWithOneButton(context,
                            "Select Order Validity", null)
                    }
                } else {
                    result = false
                    Utilities.showDialogWithOneButton(context,
                        "Price should be in multiples of $tickValue", null)
                }
            } else {
                binding.qtyET.error = "Field should not be empty"
            }
        } else {
            binding.priceET.error = "Field should not be empty"
        }
        return result
    }
    private fun setValidityAndTypeData() {
        val tableRow = TableRow(context)
        tableRow.layoutParams = binding.validityTableLayout.layoutParams // TableLayout is the parent view

        val rowParams1 = TableRow.LayoutParams(1)
        val rowParams2 = TableRow.LayoutParams(2)
        val rowParams3 = TableRow.LayoutParams(3)
        val rowParams4 = TableRow.LayoutParams(4)
        rowParams1.setMargins(10, 10, 10, 10)
        rowParams2.setMargins(10, 10, 10, 10)
        rowParams3.setMargins(10, 10, 10, 10)
        rowParams4.setMargins(10, 10, 10, 10)

        val exchangeOptions = (requireActivity().application as FinsolApplication).getExchangeOptions()

        for(exchangeOption in exchangeOptions) {
            Log.e("exchange options:name:", exchangeOption.ExchangeName)

            if(contractsModel?.exchangeName.equals(exchangeOption.ExchangeName)) {
                var typeTableRow = TableRow(context)
                typeTableRow.layoutParams = binding.typeTableLayout.layoutParams
                for(typeIndex in exchangeOption.OrderTypes.indices){
                    Log.e("order type:",exchangeOption.OrderTypes.get(typeIndex)+"")
                    if (typeIndex == 4 || typeIndex == 8) {
                        binding.typeTableLayout.addView(typeTableRow)
                        typeTableRow = TableRow(context)
                        typeTableRow.layoutParams =
                            binding.typeTableLayout.layoutParams // TableLayout is the parent view
                    }
                    val radioButton: RadioButton =
                        layoutInflater.inflate(R.layout.view_radio_button, null) as RadioButton
                    radioButton.text = exchangeOption.OrderTypes[typeIndex]
                    radioButton.layoutParams = rowParams1
                    typeTableRow.addView(radioButton)
                }
                binding.typeTableLayout.addView(typeTableRow)
                binding.typeTableLayout.setClickListener { rbTypeClicked() }

                var tableRow = TableRow(context)
                tableRow.layoutParams = binding.validityTableLayout.layoutParams
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


                    when(itemIndex){
                        0 ->  radioButton.layoutParams = rowParams1
                        1 ->  radioButton.layoutParams = rowParams2
                        2 ->  radioButton.layoutParams = rowParams3
                        3 ->  radioButton.layoutParams = rowParams4
                        4 ->  radioButton.layoutParams = rowParams1
                        5 ->  radioButton.layoutParams = rowParams2
                        6 ->  radioButton.layoutParams = rowParams3
                        7 ->  radioButton.layoutParams = rowParams4
                    }
                    // TableRow is the parent view
                    tableRow.addView(radioButton)
                }
                binding.validityTableLayout.addView(tableRow)
            }

        }
    }

}
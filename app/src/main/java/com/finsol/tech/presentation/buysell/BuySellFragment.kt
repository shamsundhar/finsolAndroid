package com.finsol.tech.presentation.buysell

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
import androidx.navigation.fragment.findNavController
import com.finsol.tech.FinsolApplication
import com.finsol.tech.R
import com.finsol.tech.data.model.Contracts
import com.finsol.tech.data.model.OrderHistoryModel
import com.finsol.tech.databinding.FragmentBuySellBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.watchlist.adapter.ChildWatchListAdapter1
import com.finsol.tech.util.AppConstants.KEY_PREF_DARK_MODE
import com.finsol.tech.util.PreferenceHelper
import com.finsol.tech.util.ToggleButtonGroupTableLayout
import com.finsol.tech.util.Utilities


class BuySellFragment: BaseFragment() {
    private lateinit var binding: FragmentBuySellBinding
    private lateinit var preferenceHelper: PreferenceHelper
    private var contractsModel:Contracts? = null
    private var orderHistoryModel:OrderHistoryModel? = null
    private var isDarkMode: Boolean = false
    private var mode: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBuySellBinding.inflate(inflater, container, false)
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()
        mode = arguments?.getString("selectedMode")
        orderHistoryModel = arguments?.getParcelable("selectedModel")
        contractsModel = arguments?.getParcelable("selectedContractsModel")
        setInitialData()
        setOrderHistoryData()
        setContractsData()
        setValidityAndTypeData()
        applyDarkModeData()
        setClickListeners()

        return binding.root
    }

    private fun setOrderHistoryData() {
//        binding.tickValue.text = orderHistoryModel.
    }
    private fun setContractsData() {
        binding.tickValue.text = contractsModel?.tickSize.toString()
        binding.lotValue.text = contractsModel?.lotSize.toString()

        val change = contractsModel?.lTP?.minus(contractsModel?.closePrice!!)
        val changePercent:Float
        if(contractsModel?.closePrice != 0){
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

    }

    private fun setClickListeners() {
        binding.radioGroupBuySell.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = group.findViewById<View>(checkedId) as RadioButton
            val isChecked = checkedRadioButton.isChecked
            if (isChecked) {
                if(checkedRadioButton.text.equals("Buy")){
                    if(!isDarkMode)binding.rootLayout.setBackgroundColor(resources.getColor(R.color.colorSecondary))
                } else {
                    if(!isDarkMode)binding.rootLayout.setBackgroundColor(resources.getColor(R.color.lavender_blush))
                }
            }
        }
        binding.toolbar.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.confirmButton.setOnClickListener {
            if(validate()){
                findNavController().navigate(R.id.ordersFragment)
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
        var result:Boolean = false
        val price = binding.priceET.text.toString()
        val quantity = binding.qtyET.text.toString()
        val tickValue = binding.tickValue.text.toString()
        if(price.isNotBlank()){
            binding.priceET.error = null
            if(quantity.isNotBlank()){
                binding.qtyET.error = null
                if((price.toDouble())%(tickValue.toDouble()) == 0.0){
                    result = true
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
                    Log.e("time in forces:", "" + exchangeOption.TimeInForces.get(itemIndex))
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
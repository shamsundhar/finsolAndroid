package com.finsol.tech.presentation.buysell

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TableRow
import androidx.core.view.marginEnd
import androidx.navigation.fragment.findNavController
import com.finsol.tech.FinsolApplication
import com.finsol.tech.R
import com.finsol.tech.data.model.Contracts
import com.finsol.tech.data.model.OrderHistoryModel
import com.finsol.tech.databinding.FragmentBuySell2Binding
import com.finsol.tech.databinding.FragmentBuySellBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.util.AppConstants.KEY_PREF_DARK_MODE
import com.finsol.tech.util.PreferenceHelper


class BuySellFragment: BaseFragment() {
    private lateinit var binding: FragmentBuySell2Binding
    private lateinit var preferenceHelper: PreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBuySell2Binding.inflate(inflater, container, false)
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()
        val mode: String? = arguments?.getString("selectedMode")
        val model: OrderHistoryModel? = arguments?.getParcelable("selectedModel")
        val contractsModel: Contracts? = arguments?.getParcelable("selectedContractsModel")
        setInitialData(model)

        val isDarkMode = preferenceHelper.getBoolean(context, KEY_PREF_DARK_MODE, false)
        if(mode.equals("Buy")){
            binding.radioButtonBuy.isChecked = true
            if(!isDarkMode)binding.rootLayout.setBackgroundColor(resources.getColor(R.color.colorSecondary))
        } else{
            binding.radioButtonSell.isChecked = true
            if(!isDarkMode)binding.rootLayout.setBackgroundColor(resources.getColor(R.color.lavender_blush))
        }

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


        binding.toolbar.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
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

        binding.confirmButton.setOnClickListener {
            findNavController().navigate(R.id.ordersFragment)
        }

        return binding.root
    }

    private fun setInitialData(model: OrderHistoryModel?) {
        binding.toolbar.title2.visibility = View.VISIBLE
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.marginLayout.visibility = View.GONE
    }

}
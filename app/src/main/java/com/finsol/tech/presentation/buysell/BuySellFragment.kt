package com.finsol.tech.presentation.buysell

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.navigation.fragment.findNavController
import com.finsol.tech.R
import com.finsol.tech.data.model.OrderHistoryModel
import com.finsol.tech.databinding.FragmentBuySellBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.util.AppConstants.KEY_PREF_DARK_MODE
import com.finsol.tech.util.PreferenceHelper


class BuySellFragment: BaseFragment() {
    private lateinit var binding: FragmentBuySellBinding
    private lateinit var preferenceHelper: PreferenceHelper
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
        val mode: String? = arguments?.getString("selectedMode")
        val model: OrderHistoryModel? = arguments?.getParcelable("selectedModel")
        setInitialData(model)

        val isDarkMode = preferenceHelper.getBoolean(context, KEY_PREF_DARK_MODE, false)
        if(mode.equals("Buy")){
            binding.radioButtonBuy.isChecked = true
            if(!isDarkMode)binding.rootLayout.setBackgroundColor(resources.getColor(R.color.colorSecondary))
        } else{
            binding.radioButtonSell.isChecked = true
            if(!isDarkMode)binding.rootLayout.setBackgroundColor(resources.getColor(R.color.lavender_blush))
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
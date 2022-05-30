package com.finsol.tech.presentation.portfolio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.navigation.fragment.findNavController
import com.finsol.tech.R
import com.finsol.tech.data.model.PortfolioData
import com.finsol.tech.databinding.DialogBottomPortfolioItemDetailsBinding
import com.finsol.tech.util.AppConstants
import com.finsol.tech.util.PreferenceHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class PortfolioBottomSheetDialog: BottomSheetDialogFragment() {
    private lateinit var binding:DialogBottomPortfolioItemDetailsBinding
    private lateinit var preferenceHelper: PreferenceHelper
    private var exchangeMap:HashMap<String, String> = HashMap()
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()
        binding = DialogBottomPortfolioItemDetailsBinding.inflate(inflater, container, false)

        val model: PortfolioData? = arguments?.getParcelable("selectedModel")
        exchangeMap = preferenceHelper.loadMap(context, AppConstants.KEY_PREF_EXCHANGE_MAP)
        binding.symbolDetails.symbolName.text = model?.productSymbol
        binding.symbolDetails.exchangeLabel.text = exchangeMap.get(model?.exchangeName.toString())
        binding.symbolDetails.exchangeValue.text = model?.cumulativePNL.toString()
        binding.symbolDetails.exchangePercent.text = java.lang.String.format(resources.getString(R.string.text_cumulative_pnl), model?.cumulativePNL)+"%"

        binding.addButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dismiss()
            }
        })
        binding.exitButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dismiss()
            }
        })
        binding.viewMoreDetails.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dismiss()
                val bundle = Bundle()
                bundle.putParcelable("selectedModel", model)
                findNavController().navigate(R.id.portfolioDetailsFragment, bundle)
            }
        })
        return binding.root
    }

}
package com.finsol.tech.presentation.portfolio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.navigation.fragment.findNavController
import com.finsol.tech.FinsolApplication
import com.finsol.tech.R
import com.finsol.tech.data.model.GetAllContractsResponse
import com.finsol.tech.data.model.PendingOrderModel
import com.finsol.tech.data.model.PortfolioData
import com.finsol.tech.databinding.DialogBottomPortfolioItemDetailsBinding
import com.finsol.tech.util.AppConstants
import com.finsol.tech.util.PreferenceHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class PortfolioBottomSheetDialog: BottomSheetDialogFragment() {
    private lateinit var binding:DialogBottomPortfolioItemDetailsBinding
    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var allContractsResponse: GetAllContractsResponse
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
        binding.symbolDetails.exchangeLabel.text = exchangeMap[model?.exchangeName.toString()]
        model?.exchangeNameString = exchangeMap[model?.exchangeName.toString()].toString()
        setTickAndLotData(model)
        binding.symbolDetails.exchangeValue.text = model?.cumulativePNL.toString()
        //TODO display ltp and percentage in below percent
        val text = model?.LTP +"("+")"
        binding.symbolDetails.exchangePercent.text = java.lang.String.format(resources.getString(R.string.text_cumulative_pnl), model?.cumulativePNL)+"%"

        binding.addButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if(model?.netPosition!! > 0) {
                    val bundle = Bundle()
                    model.price = model.LTP
                    model.quantity = "1"
                    bundle.putString("selectedMode", "Buy")
                    bundle.putString("fromScreen", "Portfolio")
                    bundle.putParcelable("selectedPortfolioModel", model)
                    findNavController().navigate(R.id.to_buySellFragmentFromPortfolioBottom, bundle)
                    dismiss()
                } else {
                    val bundle = Bundle()
                    model.price = model.LTP
                    model.quantity = "1"
                    bundle.putString("selectedMode", "Sell")
                    bundle.putString("fromScreen", "Portfolio")
                    bundle.putParcelable("selectedPortfolioModel", model)
                    findNavController().navigate(R.id.to_buySellFragmentFromPortfolioBottom, bundle)
                    dismiss()
                }
            }
        })
        binding.exitButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if(model?.netPosition!! > 0) {
                    val bundle = Bundle()
                    model.price = model.LTP
                    model.quantity = model.netPosition.toString()
                    bundle.putString("selectedMode", "Sell")
                    bundle.putString("fromScreen", "Portfolio")
                    bundle.putParcelable("selectedPortfolioModel", model)
                    findNavController().navigate(R.id.to_buySellFragmentFromPortfolioBottom, bundle)
                    dismiss()
                } else {
                    val bundle = Bundle()
                    model.price = model.LTP
                    model.quantity = model.netPosition.toString()
                    bundle.putString("selectedMode", "Buy")
                    bundle.putString("fromScreen", "Portfolio")
                    bundle.putParcelable("selectedPortfolioModel", model)
                    findNavController().navigate(R.id.to_buySellFragmentFromPortfolioBottom, bundle)
                    dismiss()
                }
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
    private fun setTickAndLotData(model: PortfolioData?) {
        allContractsResponse =
            (requireActivity().application as FinsolApplication).getAllContracts()
        allContractsResponse.allContracts = allContractsResponse.allContracts +
                allContractsResponse.watchlist1 +
                allContractsResponse.watchlist2 +
                allContractsResponse.watchlist3
        val contract = allContractsResponse.allContracts.find {
            it.securityID == model?.securityID.toString()
        }
        model?.tickSize = contract?.tickSize.toString()
        model?.lotSize = contract?.lotSize.toString()
   }

}
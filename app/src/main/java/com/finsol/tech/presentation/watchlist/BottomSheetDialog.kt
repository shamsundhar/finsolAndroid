package com.finsol.tech.presentation.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.navigation.fragment.findNavController
import com.finsol.tech.R
import com.finsol.tech.data.model.Contracts
import com.finsol.tech.databinding.DialogBottomWatchlistItemDetailsBinding
import com.finsol.tech.rabbitmq.MySingletonViewModel
import com.finsol.tech.util.Utilities
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetDialog: BottomSheetDialogFragment() {
    private lateinit var binding:DialogBottomWatchlistItemDetailsBinding
    private lateinit var mySingletonViewModel: MySingletonViewModel
    private var model:Contracts? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {

        binding = DialogBottomWatchlistItemDetailsBinding.inflate(inflater, container, false)

        model = arguments?.getParcelable("selectedModel")

        mySingletonViewModel  = MySingletonViewModel.getMyViewModel(this)

        mySingletonViewModel.getMarketData()?.observe(viewLifecycleOwner) {
            val securityID = model?.securityID
            val markertData = it[model?.securityID]
            if(securityID.equals(markertData?.securityID,true)){
                model?.lTP = markertData?.LTP?.toDouble() ?: 0.0
                model?.updatedTime = Utilities.getCurrentTime()
                displayChangedMarketData()
            }
        }

        displayChangedMarketData()
        binding.symbolDetails.symbolName.text = model?.symbolName
        binding.symbolDetails.symbolCity.text = model?.exchangeName

        binding.buyButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val bundle = Bundle()
                bundle.putString("selectedMode", "Buy")
                bundle.putParcelable("selectedContractsModel", model)
                findNavController().navigate(R.id.buySellFragment, bundle)
                dismiss()
            }
        })
        binding.sellButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val bundle = Bundle()
                bundle.putString("selectedMode", "Sell")
                bundle.putParcelable("selectedContractsModel", model)
                findNavController().navigate(R.id.buySellFragment, bundle)
                dismiss()
            }
        })
        binding.viewMoreDetails.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dismiss()
                val bundle = Bundle()
                bundle.putParcelable("selectedContractsModel", model)
                findNavController().navigate(R.id.to_watchListSymbolDetailsFragment, bundle)
            }
        })
        return binding.root
    }

    private fun displayChangedMarketData() {
        val change = model?.lTP?.minus(model?.closePrice!!)
        val changePercent:Float
        if(model?.closePrice != 0f){
            if (change != null) {
                changePercent = ((change/ model?.closePrice!!)*100).toFloat()
            } else{
                changePercent = 0.0F
            }
        }
        else {
            changePercent = ((change)?.times(100))?.toFloat()!!
        }

        binding.symbolDetails.symbolPrice.text = model?.closePrice.toString()
        binding.symbolDetails.symbolTime.text = model?.updatedTime
        binding.symbolDetails.symbolValue.text = java.lang.String.format(resources.getString(R.string.text_cumulative_pnl), changePercent)+"%"
    }

}
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetDialog: BottomSheetDialogFragment() {
    private lateinit var binding:DialogBottomWatchlistItemDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {

        binding = DialogBottomWatchlistItemDetailsBinding.inflate(inflater, container, false)

        val model: Contracts? = arguments?.getParcelable("selectedModel")

        val change = model?.lTP?.minus(model?.closePrice!!)
        val changePercent:Float
        if(model?.closePrice != 0){
            if (change != null) {
                changePercent = ((change/ model?.closePrice!!)*100).toFloat()
            } else{
                changePercent = 0.0F
            }
        }
        else {
            changePercent = ((change)?.times(100))?.toFloat()!!
        }

        binding.symbolDetails.symbolName.text = model?.symbolName
        binding.symbolDetails.symbolPrice.text = model?.closePrice.toString()
        binding.symbolDetails.symbolTime.text = model?.updatedTime
        binding.symbolDetails.symbolCity.text = model?.exchangeName
        binding.symbolDetails.symbolValue.text = changePercent.toString()+"%"

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

}
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
        binding.symbolDetails.symbolName.text = model?.symbolName
        binding.symbolDetails.symbolPrice.text = model?.closePrice.toString()
        binding.symbolDetails.symbolTime.text = model?.symbolName
        binding.symbolDetails.symbolCity.text = model?.symbolName
        binding.symbolDetails.symbolValue.text = model?.symbolName

        binding.buyButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val bundle = Bundle()
                bundle.putString("selectedMode", "Buy")
                findNavController().navigate(R.id.buySellFragment, bundle)
                dismiss()
            }
        })
        binding.sellButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val bundle = Bundle()
                bundle.putString("selectedMode", "Sell")
                findNavController().navigate(R.id.buySellFragment, bundle)
                dismiss()
            }
        })
        binding.viewMoreDetails.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dismiss()
                val bundle = Bundle()
                bundle.putParcelable("selectedModel", model)
                findNavController().navigate(R.id.to_watchListSymbolDetailsFragment, bundle)
            }
        })
        return binding.root
    }

}
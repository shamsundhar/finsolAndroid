package com.finsol.tech.presentation.portfolio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.navigation.fragment.findNavController
import com.finsol.tech.R
import com.finsol.tech.databinding.DialogBottomPortfolioItemDetailsBinding
import com.finsol.tech.databinding.DialogBottomWatchlistItemDetailsBinding
import com.finsol.tech.presentation.watchlist.WatchListModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class PortfolioBottomSheetDialog: BottomSheetDialogFragment() {
    private lateinit var binding:DialogBottomPortfolioItemDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {

        binding = DialogBottomPortfolioItemDetailsBinding.inflate(inflater, container, false)

        val model: WatchListModel? = arguments?.getParcelable("selectedModel")

        binding.symbolDetails.symbolName.text = model?.symbolName
        binding.symbolDetails.exchangeLabel.text = "BSE"
        binding.symbolDetails.exchangeValue.text = "-2378"
        binding.symbolDetails.exchangePercent.text = "-9.5(-0.34%)"

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
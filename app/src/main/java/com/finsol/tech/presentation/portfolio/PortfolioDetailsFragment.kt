package com.finsol.tech.presentation.portfolio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.finsol.tech.R
import com.finsol.tech.data.model.PortfolioData
import com.finsol.tech.databinding.FragmentPortfolioDetailsBinding
import com.finsol.tech.presentation.base.BaseFragment

class PortfolioDetailsFragment: BaseFragment() {
    private lateinit var binding: FragmentPortfolioDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPortfolioDetailsBinding.inflate(inflater, container, false)

        val model: PortfolioData? = arguments?.getParcelable("selectedModel")
        setInitialData(model)

        binding.toolbar.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.buyButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("selectedMode", "Buy")
            findNavController().navigate(R.id.buySellFragment, bundle)
        }
        binding.sellButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("selectedMode", "Sell")
            findNavController().navigate(R.id.buySellFragment, bundle)
        }

        return binding.root
    }
    private fun setInitialData(model: PortfolioData?){
        binding.portfolioDetails.exchangeLabel.text = ""
        binding.portfolioDetails.exchangePercent.text = ""
        binding.portfolioDetails.exchangeValue.text = ""
        binding.portfolioDetails.symbolName.text = model?.productSymbol
        binding.toolbar.title2.visibility = View.VISIBLE
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.title2.setText(R.string.text_portfolio_details)
    }
}
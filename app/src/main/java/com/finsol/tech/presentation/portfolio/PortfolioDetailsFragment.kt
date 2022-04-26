package com.finsol.tech.presentation.portfolio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.finsol.tech.R
import com.finsol.tech.databinding.FragmentPortfolioDetailsBinding
import com.finsol.tech.databinding.FragmentWatchlistSymbolDetailsBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.watchlist.WatchListModel

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

        val model: WatchListModel? = arguments?.getParcelable("selectedModel")
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
    private fun setInitialData(model: WatchListModel?){
        binding.portfolioDetails.exchangeLabel.text = "BSE"
        binding.portfolioDetails.exchangePercent.text = "-9.5(-0.34%)"
        binding.portfolioDetails.exchangeValue.text = " - 2876"
        binding.portfolioDetails.symbolName.text = "Peuget"
        binding.toolbar.subTitle.visibility = View.GONE
        binding.toolbar.title.visibility = View.GONE
        binding.toolbar.title2.visibility = View.VISIBLE
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.title2.setText(R.string.text_portfolio_details)
    }
}
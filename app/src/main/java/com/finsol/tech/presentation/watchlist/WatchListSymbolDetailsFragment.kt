package com.finsol.tech.presentation.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.finsol.tech.R
import com.finsol.tech.data.model.Contracts
import com.finsol.tech.data.model.Market
import com.finsol.tech.databinding.FragmentWatchlistSymbolDetailsBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.prelogin.LoginMarketViewState
import com.finsol.tech.presentation.prelogin.LoginViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class WatchListSymbolDetailsFragment: BaseFragment() {

    private lateinit var binding: FragmentWatchlistSymbolDetailsBinding
    private lateinit var wlsdViewModel: WatchListSymbolDetailsViewModel
    private var bidViews: ArrayList<MarketDepthViews> = ArrayList()
    private var offerViews: ArrayList<MarketDepthViews> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWatchlistSymbolDetailsBinding.inflate(inflater, container, false)
        wlsdViewModel = ViewModelProvider(requireActivity()).get(WatchListSymbolDetailsViewModel::class.java)


        val model: Contracts? = arguments?.getParcelable("selectedModel")
        setInitialData(model)

        addBidOfferViews()

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

    private fun addBidOfferViews() {
        bidViews.clear()
        bidViews.add(MarketDepthViews(binding.bidPrice1,binding.bidOrder1,binding.bidQty1))
        bidViews.add(MarketDepthViews(binding.bidPrice2,binding.bidOrder2,binding.bidQty2))
        bidViews.add(MarketDepthViews(binding.bidPrice3,binding.bidOrder3,binding.bidQty3))
        bidViews.add(MarketDepthViews(binding.bidPrice4,binding.bidOrder4,binding.bidQty4))
        bidViews.add(MarketDepthViews(binding.bidPrice5,binding.bidOrder5,binding.bidQty5))

        offerViews.clear()
        offerViews.add(MarketDepthViews(binding.offerPrice1,binding.offerOrder1,binding.offerQty1))
        offerViews.add(MarketDepthViews(binding.offerPrice2,binding.offerOrder2,binding.offerQty2))
        offerViews.add(MarketDepthViews(binding.offerPrice3,binding.offerOrder3,binding.offerQty3))
        offerViews.add(MarketDepthViews(binding.offerPrice4,binding.offerOrder4,binding.offerQty4))
        offerViews.add(MarketDepthViews(binding.offerPrice5,binding.offerOrder5,binding.offerQty5))

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        val model: Contracts? = arguments?.getParcelable("selectedModel")
        wlsdViewModel.fetchMarketData(model?.securityID.toString(),model?.exchangeName.toString())
    }

    private fun initObservers() {
        wlsdViewModel.mState
            .flowWithLifecycle(lifecycle,  Lifecycle.State.STARTED)
            .onEach {
                    it -> processResponse(it)
            }
            .launchIn(lifecycleScope)
    }

    private fun processResponse(state: WatchListSymbolDetailsState) {
        when(state){
            is WatchListSymbolDetailsState.SuccessResponse -> handleMarketDataResponse(state.marketDetails)

        }
    }

    private fun handleMarketDataResponse(marketDetails: Market) {
        updateBidOfferViewsData(marketDetails)
    }

    private fun updateBidOfferViewsData(marketDetails: Market) {
        marketDetails.askPrice.forEachIndexed { index, element ->
            offerViews[index].view1.text = element[0].toString()
            offerViews[index].view3.text = element[1].toString()
        }

        marketDetails.bidPrice.forEachIndexed { index, element ->
            bidViews[index].view1.text = element[0].toString()
            bidViews[index].view3.text = element[1].toString()
        }
    }

    private fun setInitialData(model: Contracts?){
        binding.symbolDetails.symbolName.text = model?.symbolName
        binding.symbolDetails.symbolPrice.text = model?.symbolName
        binding.symbolDetails.symbolTime.text = model?.symbolName
        binding.symbolDetails.symbolCity.text = model?.symbolName
        binding.symbolDetails.symbolValue.text = model?.symbolName
        binding.toolbar.subTitle.visibility = View.GONE
        binding.toolbar.title.visibility = View.GONE
        binding.toolbar.title2.visibility = View.VISIBLE
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.title2.setText(R.string.text_market_watch)
    }

    data class MarketDepthViews(val view1 : TextView,val view2 : TextView,val view3 : TextView)
}
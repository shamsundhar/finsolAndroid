package com.finsol.tech.presentation.watchlist

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
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
import com.finsol.tech.presentation.orders.PendingOrderDetailsViewState
import com.finsol.tech.rabbitmq.MySingletonViewModel
import com.finsol.tech.util.Utilities
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.HashMap

class WatchListSymbolDetailsFragment : BaseFragment() {

    private var model: Contracts? = null
    private lateinit var mySingletonViewModel: MySingletonViewModel
    private var isObserversInitialized: Boolean = false
    private lateinit var binding: FragmentWatchlistSymbolDetailsBinding
    private lateinit var wlsdViewModel: WatchListSymbolDetailsViewModel
    private lateinit var progressDialog: ProgressDialog
    private var bidViews: ArrayList<MarketDepthViews> = ArrayList()
    private var offerViews: ArrayList<MarketDepthViews> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        val model: Contracts? = arguments?.getParcelable("selectedContractsModel")
        wlsdViewModel.fetchMarketData(model?.securityID.toString(), model?.exchangeName.toString())
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWatchlistSymbolDetailsBinding.inflate(inflater, container, false)
        progressDialog = ProgressDialog(
            context,
            R.style.AppTheme_Dark_Dialog
        )
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(getString(R.string.text_please_wait))
        wlsdViewModel =
            ViewModelProvider(requireActivity()).get(WatchListSymbolDetailsViewModel::class.java)
        model = arguments?.getParcelable("selectedContractsModel")
        setInitialData(model)

        addBidOfferViews()

        binding.toolbar.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.buyButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("selectedMode", "Buy")
            bundle.putParcelable("selectedContractsModel", model)
            findNavController().navigate(R.id.buySellFragment, bundle)
        }
        binding.sellButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("selectedMode", "Sell")
            bundle.putParcelable("selectedContractsModel", model)
            findNavController().navigate(R.id.buySellFragment, bundle)
        }
        mySingletonViewModel  = MySingletonViewModel.getMyViewModel(this)

        return binding.root
    }

    private fun updateListWithNewMarketData(it: HashMap<String, Market>?) {
        val market = it?.get(model?.securityID)
        market?.let {
            wlsdViewModel.updateMarketDataFromSocket(it)
        }
    }


    private fun addBidOfferViews() {
        bidViews.clear()
        bidViews.add(MarketDepthViews(binding.bidPrice1, binding.bidQty1))
        bidViews.add(MarketDepthViews(binding.bidPrice2, binding.bidQty2))
        bidViews.add(MarketDepthViews(binding.bidPrice3, binding.bidQty3))
        bidViews.add(MarketDepthViews(binding.bidPrice4, binding.bidQty4))
        bidViews.add(MarketDepthViews(binding.bidPrice5, binding.bidQty5))

        offerViews.clear()
        offerViews.add(MarketDepthViews(binding.offerPrice1, binding.offerQty1))
        offerViews.add(MarketDepthViews(binding.offerPrice2, binding.offerQty2))
        offerViews.add(MarketDepthViews(binding.offerPrice3, binding.offerQty3))
        offerViews.add(MarketDepthViews(binding.offerPrice4, binding.offerQty4))
        offerViews.add(MarketDepthViews(binding.offerPrice5, binding.offerQty5))

    }

    private fun initObservers() {
        if (isObserversInitialized) {
            return
        }
        isObserversInitialized = true
        wlsdViewModel.mState
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { it ->
                processResponse(it)
            }
            .launchIn(lifecycleScope)
    }

    private fun processResponse(state: WatchListSymbolDetailsState) {
        when (state) {
            is WatchListSymbolDetailsState.MarketDataSuccessResponse -> handleMarketDataResponseFromRestAPI(state.marketDetails)
            is WatchListSymbolDetailsState.MarketDataSocketSuccessResponse -> updateBidOfferViewsData(state.marketDetails)
            is WatchListSymbolDetailsState.IsLoading -> handleLoading(state.isLoading)
        }
    }
    private fun handleLoading(isLoading: Boolean) {
        if(isLoading){
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }
    private fun handleMarketDataResponseFromRestAPI(marketDetails: Market) {
        mySingletonViewModel.getMarketData()?.observe(viewLifecycleOwner) {
            updateListWithNewMarketData(it)
        }
        updateBidOfferViewsData(marketDetails)
    }

    private fun updateBidOfferViewsData(marketDetails: Market) {
        marketDetails.askPrice.forEachIndexed { index, element ->
            if(index < 5) {
                offerViews[index].view1.text = element[0].toString()
                offerViews[index].view2.text = element[1].toInt().toString()
                offerViews[index].view1.setOnClickListener(View.OnClickListener {
                    val bundle = Bundle()
                    bundle.putString("selectedMode", "Sell")
                    bundle.putParcelable("selectedContractsModel", model)
                    findNavController().navigate(R.id.buySellFragment, bundle)
                })
            }
        }

        marketDetails.bidPrice.forEachIndexed { index, element ->
            if(index < 5) {
                bidViews[index].view1.text = element[0].toString()
                bidViews[index].view2.text = element[1].toInt().toString()
                bidViews[index].view1.setOnClickListener(View.OnClickListener {
                    val bundle = Bundle()
                    bundle.putString("selectedMode", "Buy")
                    bundle.putParcelable("selectedContractsModel", model)
                    findNavController().navigate(R.id.buySellFragment, bundle)
                })
            }
        }
        binding.openValue.text = marketDetails.OpenPrice
        binding.highValue.text = marketDetails.HighPrice
        binding.lowValue.text = marketDetails.LowPrice
        binding.closeValue.text = marketDetails.ClosePrice
        binding.volumeValue.text = marketDetails.Volume
        binding.symbolDetails.symbolTime.text = Utilities.getCurrentTime().toString()
        binding.symbolDetails.symbolPrice.text = marketDetails.LTP
    }

    private fun updateLTPAndPercent(model: Contracts?) {
        val change = model?.lTP?.minus(model.closePrice)
        val changePercent:Float
        if(model?.closePrice != 0f){
            changePercent = if (change != null) {
                ((change/ model.closePrice)*100).toFloat()
            } else{
                0.0F
            }
        }
        else {
            changePercent = ((change)?.times(100))?.toFloat()!!
        }
        if (change != null) {
            if(change >= 0.0){
                context?.let {binding.symbolDetails.symbolValue.setTextColor( ContextCompat.getColor(it,(R.color.green)) )}
            } else {
                context?.let {binding.symbolDetails.symbolValue.setTextColor( ContextCompat.getColor(it,(R.color.red)) )}
            }
        }
        binding.symbolDetails.symbolValue.text =  java.lang.String.format(
            resources.getString(R.string.text_cumulative_pnl),
            change
        )+"(" + java.lang.String.format(
            resources.getString(R.string.text_cumulative_pnl),
            changePercent
        ) + "%)"
        binding.symbolDetails.symbolPrice.text = model?.lTP.toString()
    }

    private fun setInitialData(model: Contracts?) {
        updateLTPAndPercent(model)
        binding.symbolDetails.symbolName.text = model?.symbolName
        binding.symbolDetails.symbolTime.text = model?.updatedTime
        binding.symbolDetails.symbolCity.text = model?.exchangeName

        binding.toolbar.title2.visibility = View.VISIBLE
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.title2.setText(R.string.text_market_watch)
    }

    data class MarketDepthViews(val view1: TextView, val view2: TextView)
}
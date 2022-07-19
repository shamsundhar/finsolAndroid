package com.finsol.tech.presentation.portfolio

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.finsol.tech.R
import com.finsol.tech.data.model.Market
import com.finsol.tech.data.model.PortfolioData
import com.finsol.tech.databinding.FragmentPortfolioDetailsBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.watchlist.WatchListSymbolDetailsFragment
import com.finsol.tech.rabbitmq.MySingletonViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.math.abs

class PortfolioDetailsFragment : BaseFragment() {
    private lateinit var mySingletonViewModel: MySingletonViewModel
    private lateinit var binding: FragmentPortfolioDetailsBinding
    private lateinit var progressDialog: ProgressDialog
    private var isObserversInitialized: Boolean = false
    private lateinit var viewModel: PortfolioDetailsViewModel
    private var bidViews: ArrayList<WatchListSymbolDetailsFragment.MarketDepthViews> = ArrayList()
    private var offerViews: ArrayList<WatchListSymbolDetailsFragment.MarketDepthViews> = ArrayList()
    private var model: PortfolioData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        viewModel.fetchMarketData(model?.securityID.toString(), model?.exchangeName.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPortfolioDetailsBinding.inflate(inflater, container, false)
        mySingletonViewModel = MySingletonViewModel.getMyViewModel(this)
        viewModel =
            ViewModelProvider(requireActivity()).get(PortfolioDetailsViewModel::class.java)
        model = arguments?.getParcelable("selectedModel")
        progressDialog = ProgressDialog(
            context,
            R.style.AppTheme_Dark_Dialog
        )
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(getString(R.string.text_please_wait))
        addBidOfferViews()
        setInitialData(model)

        binding.toolbar.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
//        binding.buyButton.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putString("selectedMode", "Buy")
//            findNavController().navigate(R.id.buySellFragment, bundle)
//        }
//        binding.sellButton.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putString("selectedMode", "Sell")
//            findNavController().navigate(R.id.buySellFragment, bundle)
//        }
        binding.buyButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                if(model?.netPosition!! > 0) {
                    val bundle = Bundle()
                    model?.price = model?.LTP.toString()
                    model?.quantity = "1"
                    bundle.putString("selectedMode", "Buy")
                    bundle.putString("fromScreen", "Portfolio")
                    bundle.putParcelable("selectedPortfolioModel", model)
                    //TODO
                    findNavController().navigate(R.id.to_buySellFragmentFromPortfolioDetails, bundle)
                } else {
                    val bundle = Bundle()
                    model?.price = model?.LTP.toString()
                    model?.quantity = "1"
                    bundle.putString("selectedMode", "Sell")
                    bundle.putString("fromScreen", "Portfolio")
                    bundle.putParcelable("selectedPortfolioModel", model)
                    //TODO
                    findNavController().navigate(R.id.to_buySellFragmentFromPortfolioDetails, bundle)
                }
            }
        })
        binding.sellButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if(model?.netPosition!! > 0) {
                    val bundle = Bundle()
                    model?.price = model?.LTP.toString()
                    model?.quantity = model?.netPosition.toString()
                    bundle.putString("selectedMode", "Sell")
                    bundle.putString("fromScreen", "Portfolio")
                    bundle.putParcelable("selectedPortfolioModel", model)
                    //TODO
                    findNavController().navigate(R.id.to_buySellFragmentFromPortfolioDetails, bundle)
                } else {
                    val bundle = Bundle()
                    model?.price = model?.LTP.toString()
                    model?.quantity = model?.netPosition.toString()
                    bundle.putString("selectedMode", "Buy")
                    bundle.putString("fromScreen", "Portfolio")
                    bundle.putParcelable("selectedPortfolioModel", model)
                    //TODO
                    findNavController().navigate(R.id.to_buySellFragmentFromPortfolioDetails, bundle)
                }

            }
        })

        return binding.root
    }

    private fun updateViewWithMarketData(hashMap: HashMap<String, Market>) {

        val securityID = model?.securityID.toString()
        val markertData = hashMap[model?.securityID.toString()]
        if (securityID.equals(markertData?.securityID, true)) {
            model?.LTP = if (markertData?.LTP.isNullOrBlank()) {
                "-"
            } else {
                markertData?.LTP.toString()
            }
            model?.closePrice = markertData?.ClosePrice?.toFloat() ?: 0f
        }
        setInitialData(model)
        markertData?.let {
            updateBidOfferViewsData(markertData)
        }
    }

    private fun setInitialData(model: PortfolioData?) {
        binding.toolbar.title2.visibility = View.VISIBLE
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.title2.setText(R.string.text_portfolio_details)
        binding.portfolioDetails.exchangeLabel.text = model?.exchangeNameString
        binding.portfolioDetails.exchangePercent.text = if (model?.LTP?.isNotEmpty() == true) {
            val change = model.LTP.toFloat() - model.closePrice
            val changePercent: Float = if (model.closePrice != 0f) {
                ((change / model.closePrice) * 100).toFloat()
            } else {
                ((change) * 100).toFloat()
            }
            "$change(" + java.lang.String.format(
                resources.getString(R.string.text_cumulative_pnl),
                changePercent
            ) + "%)"
        } else {
            "-"
        }
        binding.portfolioDetails.exchangeValue.text = if (model?.LTP?.isNotEmpty() == true) {
            model.LTP
        } else {
            "-"
        }
        binding.portfolioDetails.symbolName.text = model?.productSymbol
        val avg: Double = if (model?.netPosition!! > 0) {
            model?.avgBuyPrice
        } else {
            model?.avgSellPrice
        }
        val invested = abs(model?.netPosition).times(avg)
        binding.investmentValue.text = java.lang.String.format(
            context?.resources?.getString(R.string.text_cumulative_pnl),
            invested
        )
        binding.currentAmountValue.text = java.lang.String.format(
            context?.resources?.getString(R.string.text_cumulative_pnl),
            (invested - model?.cumulativePNL)
        )
        binding.pnlValue.text = java.lang.String.format(
            context?.resources?.getString(R.string.text_cumulative_pnl),
            model?.cumulativePNL
        )
        binding.quantityPurchasedValue.text = abs(model.netPosition).toString()
        binding.averagePriceValue.text = java.lang.String.format(
            context?.resources?.getString(R.string.text_cumulative_pnl),
            avg
        )
        binding.daysPnlValue.text = model?.intrradayPNL.toString()
    }

    private fun addBidOfferViews() {
        bidViews.clear()
        bidViews.add(
            WatchListSymbolDetailsFragment.MarketDepthViews(
                binding.bidPrice1,
                binding.bidQty1
            )
        )
        bidViews.add(
            WatchListSymbolDetailsFragment.MarketDepthViews(
                binding.bidPrice2,
                binding.bidQty2
            )
        )
        bidViews.add(
            WatchListSymbolDetailsFragment.MarketDepthViews(
                binding.bidPrice3,
                binding.bidQty3
            )
        )
        bidViews.add(
            WatchListSymbolDetailsFragment.MarketDepthViews(
                binding.bidPrice4,
                binding.bidQty4
            )
        )
        bidViews.add(
            WatchListSymbolDetailsFragment.MarketDepthViews(
                binding.bidPrice5,
                binding.bidQty5
            )
        )

        offerViews.clear()
        offerViews.add(
            WatchListSymbolDetailsFragment.MarketDepthViews(
                binding.offerPrice1,
                binding.offerQty1
            )
        )
        offerViews.add(
            WatchListSymbolDetailsFragment.MarketDepthViews(
                binding.offerPrice2,
                binding.offerQty2
            )
        )
        offerViews.add(
            WatchListSymbolDetailsFragment.MarketDepthViews(
                binding.offerPrice3,
                binding.offerQty3
            )
        )
        offerViews.add(
            WatchListSymbolDetailsFragment.MarketDepthViews(
                binding.offerPrice4,
                binding.offerQty4
            )
        )
        offerViews.add(
            WatchListSymbolDetailsFragment.MarketDepthViews(
                binding.offerPrice5,
                binding.offerQty5
            )
        )

    }

    private fun initObservers() {
        if (isObserversInitialized) {
            return
        }
        isObserversInitialized = true
        viewModel.mState
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { it ->
                processResponse(it)
            }
            .launchIn(lifecycleScope)
    }

    private fun processResponse(state: PortfolioDetailsState) {
        when (state) {
            is PortfolioDetailsState.MarketDataSuccessResponse -> handleMarketDataResponseFromRestAPI(
                state.marketDetails
            )
            is PortfolioDetailsState.MarketDataSocketSuccessResponse -> updateBidOfferViewsData(
                state.marketDetails
            )
            is PortfolioDetailsState.ShowToast -> handleToast(state.message)
            is PortfolioDetailsState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        if (isLoading) {
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }

    private fun handleMarketDataResponseFromRestAPI(marketDetails: Market) {
        mySingletonViewModel.getMarketData()?.observe(viewLifecycleOwner) {
            updateViewWithMarketData(it)
        }
        updateBidOfferViewsData(marketDetails)
    }

    private fun updateBidOfferViewsData(marketDetails: Market) {
        marketDetails.askPrice.forEachIndexed { index, element ->
            if(index < 5) {
                offerViews[index].view1.text = element[0].toString()
                offerViews[index].view2.text = element[1].toInt().toString()
            }
        }

        marketDetails.bidPrice.forEachIndexed { index, element ->
            if(index < 5) {
                bidViews[index].view1.text = element[0].toString()
                bidViews[index].view2.text = element[1].toInt().toString()
            }
        }
        binding.openValue.text = marketDetails.OpenPrice
        binding.highValue.text = marketDetails.HighPrice
        binding.lowValue.text = marketDetails.LowPrice
        binding.closeValue.text = marketDetails.ClosePrice
        binding.volumeValue.text = marketDetails.Volume
    }

    data class MarketDepthViews(val view1: TextView, val view2: TextView)
}
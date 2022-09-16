package com.finsol.tech.presentation.orders

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
import com.finsol.tech.FinsolApplication
import com.finsol.tech.R
import com.finsol.tech.data.model.GetAllContractsResponse
import com.finsol.tech.data.model.Market
import com.finsol.tech.data.model.PendingOrderModel
import com.finsol.tech.databinding.FragmentPendingOrderDetailsBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.portfolio.PortfolioDetailsState
import com.finsol.tech.rabbitmq.MySingletonViewModel
import com.finsol.tech.util.AppConstants
import com.finsol.tech.util.PreferenceHelper
import com.finsol.tech.util.Utilities
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class OrderPendingDetailsFragment : BaseFragment() {
    private lateinit var binding: FragmentPendingOrderDetailsBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mySingletonViewModel: MySingletonViewModel
    private lateinit var allContractsResponse: GetAllContractsResponse
    private lateinit var preferenceHelper: PreferenceHelper
    private var exchangeMap: HashMap<String, String> = HashMap()
    private var isObserversInitialized: Boolean = false
    private var decimalFormatter:Int = 0
    private var model: PendingOrderModel? = null
    private lateinit var orderPendingDetailsViewModel: OrderPendingDetailsViewModel
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
        binding = FragmentPendingOrderDetailsBinding.inflate(inflater, container, false)
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()
        orderPendingDetailsViewModel =
            ViewModelProvider(requireActivity()).get(OrderPendingDetailsViewModel::class.java)
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.title2.visibility = View.VISIBLE
        model = arguments?.getParcelable("selectedModel")
        progressDialog = ProgressDialog(
            context,
            R.style.AppTheme_Dark_Dialog
        )
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(getString(R.string.text_please_wait))

        addBidOfferViews()
        setInitialData(model)
        setTickAndLotData(model)

        mySingletonViewModel = MySingletonViewModel.getMyViewModel(this)

        val text: String = model?.tickSize.toString()
        val integerPlaces = text.indexOf('.')
        val decimalPlaces = text.length - integerPlaces - 1

        decimalFormatter = when(decimalPlaces){
            1 -> R.string.format_price1
            2 -> R.string.format_price2
            3 -> R.string.format_price3
            4 -> R.string.format_price4
            5 -> R.string.format_price5
            else -> R.string.format_price2
        }

        binding.toolbar.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.cancelButton.setOnClickListener {
//            activity?.onBackPressed()
            orderPendingDetailsViewModel.cancelOrder(model?.UniqueEngineOrderID.toString())
        }
        binding.modifyButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("selectedMode", getOrderType(model))
            bundle.putString("fromScreen", "OrderPending")
            bundle.putParcelable("selectedOrderPendingModel", model)
            findNavController().navigate(R.id.to_buySellFragmentFromPendingOrderDetails, bundle)

        }
        return binding.root
    }

    private fun setInitialData(model: PendingOrderModel?) {

        exchangeMap = preferenceHelper.loadMap(context, AppConstants.KEY_PREF_EXCHANGE_MAP)
        binding.quantityValue.text = model?.OrderQty.toString()
        binding.triggerPriceValue.text = model?.TrigPrice.toString()
        binding.createdAtValue.text = Utilities.convertOrderHistoryTimeWithDate(model?.OrderTime)
        binding.symbolStatus.text = "Pending"
        binding.symbolName.text = model?.Symbol_Name
//        binding.symbolPrice.text = if (model?.LTP.isNullOrBlank()) {
//            "-"
//        } else {
//            model?.LTP.toString()
//        }
        binding.symbolPrice.text = java.lang.String.format(resources.getString(R.string.text_avg_amt), model?.PriceSend)
        binding.symbolQuantity.text = java.lang.String.format(
            resources.getString(R.string.text_work_quantity),
            model?.WorkQty
        )
        binding.status1.text = exchangeMap[model?.Exchange_Name.toString()].toString()
        binding.status2.text = getOrderType(model)
        binding.status3.text = model?.Market_Type.let {
            when (it) {
                1 -> "MARKET"
                2 -> "LIMIT"
                3 -> "STOP"
                4 -> "STOPLIMIT"
                5 -> "ICEBERG"
                else -> ""
            }
        }
        binding.triggerGroup.visibility = model?.Market_Type.let {
            when (it) {
                2 -> View.VISIBLE
                5 -> View.VISIBLE
                else -> View.GONE
            }
        }
        binding.triggerPrice.text = model?.Market_Type.let {
            when (it) {
                2 -> "Trigger Price"
                5 -> "Disclosed Quantity"
                else -> "Trigger Price"
            }
        }

        binding.toolbar.title2.visibility = View.VISIBLE
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.title2.setText(R.string.text_market_watch)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        orderPendingDetailsViewModel.fetchMarketData(
            model?.SecurityID.toString(),
            exchangeMap.get(model?.Exchange_Name.toString()).toString()
        )
    }
    private fun setTickAndLotData(model: PendingOrderModel?) {
        exchangeMap = preferenceHelper.loadMap(context, AppConstants.KEY_PREF_EXCHANGE_MAP)
        allContractsResponse =
            (requireActivity().application as FinsolApplication).getAllContracts()!!
        allContractsResponse.allContracts = allContractsResponse.allContracts +
                allContractsResponse.watchlist1 +
                allContractsResponse.watchlist2 +
                allContractsResponse.watchlist3
        val contract = allContractsResponse.allContracts.find {
            it.securityID == model?.SecurityID
        }
        model?.tickSize = contract?.tickSize.toString()
        model?.lotSize = contract?.lotSize.toString()
        model?.LTP = contract?.lTP.toString()
        if(contract?.closePrice != null){
            model?.closePrice = contract?.closePrice
        } else{
            model?.closePrice = 0.0F
        }
        model?.exchangeNameString = exchangeMap.get(model?.Exchange_Name.toString()).toString()
    }
    private fun initObservers() {
        if (isObserversInitialized) {
            return
        }
        isObserversInitialized = true
        orderPendingDetailsViewModel.mState
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { it ->
                processResponse(it)
            }
            .launchIn(lifecycleScope)
    }

    private fun processResponse(state: PendingOrderDetailsViewState) {
        when (state) {
            is PendingOrderDetailsViewState.CancelOrderSuccessResponse -> handleCancelOrderSuccessResponse()
            is PendingOrderDetailsViewState.ModifyOrderSuccessResponse -> handleModifyOrderSuccessResponse()
            is PendingOrderDetailsViewState.MarketDataSuccessResponse -> handleMarketDataResponseFromRestAPI(state.marketDetails)
            is PendingOrderDetailsViewState.MarketDataSocketSuccessResponse -> updateBidOfferViewsData(state.marketDetails)
            is PendingOrderDetailsViewState.IsLoading -> handleLoading(state.isLoading)
            is PendingOrderDetailsViewState.ShowToast -> handleToast(state.message)
        }
    }

    private fun updateListWithNewMarketData(it: java.util.HashMap<String, Market>?) {
        val market = it?.get(model?.SecurityID)
        market?.let {
            orderPendingDetailsViewModel.updateMarketDataFromSocket(it)
        }
    }

    private fun addBidOfferViews() {
        bidViews.clear()
        bidViews.add(
            MarketDepthViews(
                binding.bidPrice1,
                binding.bidQty1
            )
        )
        bidViews.add(
            MarketDepthViews(
                binding.bidPrice2,
                binding.bidQty2
            )
        )
        bidViews.add(
            MarketDepthViews(
                binding.bidPrice3,
                binding.bidQty3
            )
        )
        bidViews.add(
            MarketDepthViews(
                binding.bidPrice4,
                binding.bidQty4
            )
        )
        bidViews.add(
            MarketDepthViews(
                binding.bidPrice5,
                binding.bidQty5
            )
        )

        offerViews.clear()
        offerViews.add(
            MarketDepthViews(
                binding.offerPrice1,
                binding.offerQty1
            )
        )
        offerViews.add(
            MarketDepthViews(
                binding.offerPrice2,
                binding.offerQty2
            )
        )
        offerViews.add(
            MarketDepthViews(
                binding.offerPrice3,
                binding.offerQty3
            )
        )
        offerViews.add(
            MarketDepthViews(
                binding.offerPrice4,
                binding.offerQty4
            )
        )
        offerViews.add(
            MarketDepthViews(
                binding.offerPrice5,
                binding.offerQty5
            )
        )

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
                    model?.PriceSend = element[0].toDouble()
                    model?.OrderQty = 1
                    bundle.putString("selectedMode", "Sell")
                    bundle.putString("fromScreen", "OrderPending")
                    bundle.putParcelable("selectedOrderPendingModel", model)
                    findNavController().navigate(R.id.to_buySellFragmentFromPendingOrderDetails, bundle)
                })
                offerViews[index].view2.setOnClickListener(View.OnClickListener {
                    val bundle = Bundle()
                    model?.PriceSend = element[0].toDouble()
                    model?.OrderQty = 1
                    bundle.putString("selectedMode", "Sell")
                    bundle.putString("fromScreen", "OrderPending")
                    bundle.putParcelable("selectedOrderPendingModel", model)
                    findNavController().navigate(R.id.to_buySellFragmentFromPendingOrderDetails, bundle)
                })
            }
        }

        marketDetails.bidPrice.forEachIndexed { index, element ->
            if(index < 5) {
                bidViews[index].view1.text = element[0].toString()
                bidViews[index].view2.text = element[1].toInt().toString()
                bidViews[index].view1.setOnClickListener(View.OnClickListener {
                    val bundle = Bundle()
                    model?.PriceSend = element[0].toDouble()
                    model?.OrderQty = 1
                    bundle.putString("selectedMode", "Buy")
                    bundle.putString("fromScreen", "OrderPending")
                    bundle.putParcelable("selectedOrderPendingModel", model)
                    findNavController().navigate(R.id.to_buySellFragmentFromPendingOrderDetails, bundle)
                })
                bidViews[index].view2.setOnClickListener(View.OnClickListener {
                    val bundle = Bundle()
                    model?.PriceSend = element[0].toDouble()
                    model?.OrderQty = 1
                    bundle.putString("selectedMode", "Buy")
                    bundle.putString("fromScreen", "OrderPending")
                    bundle.putParcelable("selectedOrderPendingModel", model)
                    findNavController().navigate(R.id.to_buySellFragmentFromPendingOrderDetails, bundle)
                })
            }
        }
        binding.marketDepth2.openValue.text = formatDecimals(marketDetails.OpenPrice.toDouble())
        binding.marketDepth2.highValue.text = formatDecimals(marketDetails.HighPrice.toDouble())
        binding.marketDepth2.lowValue.text = formatDecimals(marketDetails.LowPrice.toDouble())
        binding.marketDepth2.closeValue.text = formatDecimals(marketDetails.ClosePrice.toDouble())
        binding.marketDepth3.volumeValue.text = marketDetails.Volume
        binding.marketDepth3.lowDprValue.text = formatDecimals(marketDetails.DPRLow.toDouble())
        binding.marketDepth3.highDprValue.text = formatDecimals(marketDetails.DPRHigh.toDouble())
        binding.marketDepth3.interestValue.text = marketDetails.OpenInterest
    }
    private fun formatDecimals(value: Double): String {
        return java.lang.String.format(resources.getString(decimalFormatter), value)
    }

    private fun handleCancelOrderSuccessResponse() {
        orderPendingDetailsViewModel.resetStateToDefault()
//        findNavController().navigate(R.id.ordersFragment)
        activity?.onBackPressed()
    }

    private fun handleModifyOrderSuccessResponse() {
        orderPendingDetailsViewModel.resetStateToDefault()
        findNavController().navigate(R.id.ordersFragment)
    }

    private fun handleLoading(isLoading: Boolean) {
        if (isLoading) {
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }

    private fun getOrderType(model: PendingOrderModel?): String {
        return model?.Order_Type.let {
            when (it) {
                1 -> "Buy"
                2 -> "Sell"
                else -> ""
            }
        }
    }
    data class MarketDepthViews(val view1: TextView, val view2: TextView)
}
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
import com.finsol.tech.R
import com.finsol.tech.data.model.Market
import com.finsol.tech.data.model.PendingOrderModel
import com.finsol.tech.databinding.FragmentPendingOrderDetailsBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.watchlist.WatchListSymbolDetailsFragment
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
    private var isObserversInitialized: Boolean = false
    private lateinit var preferenceHelper: PreferenceHelper
    private var exchangeMap: HashMap<String, String> = HashMap()
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

        mySingletonViewModel = MySingletonViewModel.getMyViewModel(this)

        binding.toolbar.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.cancelButton.setOnClickListener {
//            activity?.onBackPressed()
            orderPendingDetailsViewModel.cancelOrder(model?.UniqueEngineOrderID.toString())
        }
        binding.modifyButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("selectedMode", "Buy")
            findNavController().navigate(R.id.buySellFragment, bundle)
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
        binding.symbolPrice.text = if (model?.LTP.isNullOrBlank()) {
            "-"
        } else {
            model?.LTP.toString()
        }
        binding.symbolQuantity.text = java.lang.String.format(
            resources.getString(R.string.text_work_quantity),
            model?.WorkQty
        )
        binding.status1.text = exchangeMap.get(model?.Exchange_Name.toString()).toString()
        binding.status2.text = getOrderType(model)
        binding.status3.text = model?.Market_Type.let {
            when (it) {
                1 -> "MARKET"
                2 -> "LIMIT"
                3 -> "STOP"
                4 -> "STOPLIMIT"
                else -> ""
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
            offerViews[index].view1.text = element[0].toString()
            offerViews[index].view2.text = element[1].toInt().toString()
        }

        marketDetails.bidPrice.forEachIndexed { index, element ->
            bidViews[index].view1.text = element[0].toString()
            bidViews[index].view2.text = element[1].toInt().toString()
        }
        binding.openValue.text = marketDetails.OpenPrice
        binding.highValue.text = marketDetails.HighPrice
        binding.lowValue.text = marketDetails.LowPrice
        binding.closeValue.text = marketDetails.ClosePrice
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
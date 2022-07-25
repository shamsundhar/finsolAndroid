package com.finsol.tech.presentation.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.finsol.tech.FinsolApplication
import com.finsol.tech.R
import com.finsol.tech.data.model.GetAllContractsResponse
import com.finsol.tech.data.model.OrderHistoryModel
import com.finsol.tech.databinding.FragmentOrderHistoryDetailsBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.util.AppConstants
import com.finsol.tech.util.PreferenceHelper
import com.finsol.tech.util.Utilities

class OrderHistoryDetailsFragment : BaseFragment() {
    private lateinit var binding: FragmentOrderHistoryDetailsBinding
    private lateinit var allContractsResponse: GetAllContractsResponse
    private lateinit var preferenceHelper: PreferenceHelper
    private var exchangeMap: HashMap<String, String> = HashMap()
//    private val args: OrderHistoryDetailsFragmentArgs by navArgs<OrderHistoryDetailsFragmentArgs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderHistoryDetailsBinding.inflate(inflater, container, false)
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()
        val model: OrderHistoryModel? = arguments?.getParcelable("selectedModel")
        val averagePrice: String? = arguments?.getString("OrderHistoryAP")
        val filledQuantity: String? = arguments?.getString("OrderHistoryFQ")
        setInitialData(model, averagePrice, filledQuantity)
        if (model != null) {
            setTickAndLotData(model)
        }

        binding.toolbar.backButton.setOnClickListener { activity?.onBackPressed() }
        binding.repeatOrderButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("selectedMode", getOrderType(model))
            bundle.putString("fromScreen", "OrderHistory")
            bundle.putParcelable("selectedOrderHistoryModel", model)
            findNavController().navigate(R.id.to_buySellFragment, bundle)
        }

        return binding.root
    }

    private fun setTickAndLotData(model: OrderHistoryModel) {
        exchangeMap = preferenceHelper.loadMap(context, AppConstants.KEY_PREF_EXCHANGE_MAP)
        allContractsResponse =
            (requireActivity().application as FinsolApplication).getAllContracts()
        allContractsResponse.allContracts = allContractsResponse.allContracts +
                allContractsResponse.watchlist1 +
                allContractsResponse.watchlist2 +
                allContractsResponse.watchlist3
        val contract = allContractsResponse.allContracts.find {
            it.securityID == model.SecurityID
        }
        model.tickSize = contract?.tickSize.toString()
        model.lotSize = contract?.lotSize.toString()
        if(contract?.closePrice != null){
            model?.closePrice = contract?.closePrice
        } else{
            model?.closePrice = 0.0F
        }
        model.exchangeNameString = exchangeMap.get(model.Exchange_Name.toString()).toString()
    }

    private fun setInitialData(
        model: OrderHistoryModel?,
        averagePrice: String?,
        filledQuantity: String?
    ) {
        binding.toolbar.title2.visibility = View.VISIBLE
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.title2.setText(R.string.text_market_watch)
        binding.symbolName.text = model?.Symbol_Name
        binding.statusValue.text = model?.OrderStatus
        binding.timeValue.text =
            Utilities.convertOrderHistoryTimeWithDate(model?.ExchangeTransactTime)
        binding.validityValue.text = "Day"
        binding.orderIdValue.text = model?.ExchangeOderID
        binding.userValue.text = model?.UserName
        binding.status1.text = getOrderType(model)
        binding.status2.text = model?.Market_Type.let {
            when (it) {
                1 -> "MARKET"
                2 -> "LIMIT"
                3 -> "STOP"
                4 -> "STOPLIMIT"
                else -> ""
            }
        }
        binding.typeValue.text = model?.Market_Type.let {
            when (it) {
                1 -> "MARKET"
                2 -> "LIMIT"
                3 -> "STOP"
                4 -> "STOPLIMIT"
                else -> ""
            }
        }
        binding.filledQuantityValue.text = filledQuantity
        binding.averagePriceValue.text = averagePrice
        binding.timeValue2.text = Utilities.convertOrderHistoryTime(model?.ExchangeTransactTime)
        binding.idValue.text = model?.ExchangeTradingID
        binding.quantityValue.text = model?.OrderQty.toString()
        binding.priceValue.text = model?.Price.toString()
    }

    private fun getOrderType(model: OrderHistoryModel?): String {
        return model?.Order_Type.let {
            when (it) {
                1 -> "Buy"
                2 -> "Sell"
                else -> ""
            }
        }
    }
}
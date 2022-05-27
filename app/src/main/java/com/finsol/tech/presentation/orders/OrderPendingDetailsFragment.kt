package com.finsol.tech.presentation.orders

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.finsol.tech.R
import com.finsol.tech.data.model.Contracts
import com.finsol.tech.data.model.OrderHistoryModel
import com.finsol.tech.data.model.PendingOrderModel
import com.finsol.tech.databinding.FragmentPendingOrderDetailsBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.buysell.BuySellViewModel
import com.finsol.tech.presentation.buysell.BuySellViewState
import com.finsol.tech.util.AppConstants
import com.finsol.tech.util.PreferenceHelper
import com.finsol.tech.util.Utilities
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class OrderPendingDetailsFragment: BaseFragment() {
    private lateinit var binding: FragmentPendingOrderDetailsBinding
    private lateinit var progressDialog: ProgressDialog
    private var isObserversInitialized : Boolean = false
    private lateinit var preferenceHelper: PreferenceHelper
    private var exchangeMap: HashMap<String, String> = HashMap()
    private lateinit var orderPendingDetailsViewModel: OrderPendingDetailsViewModel
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
        orderPendingDetailsViewModel = ViewModelProvider(requireActivity()).get(OrderPendingDetailsViewModel::class.java)
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.title2.visibility = View.VISIBLE
//TODO("app is crashing when we pass pending order model")
        val model: PendingOrderModel? = arguments?.getParcelable("selectedModel")
        setInitialData(model)

        progressDialog = ProgressDialog(
            context,
            R.style.AppTheme_Dark_Dialog
        )
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(getString(R.string.text_please_wait))



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

//        val change = model?.lTP?.minus(model?.closePrice!!)
//        val changePercent:Float
//        if(model?.closePrice != 0){
//            if (change != null) {
//                changePercent = ((change/ model?.closePrice!!)*100).toFloat()
//            } else{
//                changePercent = 0.0F
//            }
//        }
//        else {
//            changePercent = ((change)?.times(100))?.toFloat()!!
//        }
        exchangeMap = preferenceHelper.loadMap(context, AppConstants.KEY_PREF_EXCHANGE_MAP)
        binding.quantityValue.text = model?.OrderQty.toString()
        binding.triggerPriceValue.text = model?.TrigPrice.toString()
        binding.createdAtValue.text = Utilities.convertOrderHistoryTimeWithDate(model?.OrderTime)
        binding.symbolStatus.text = "Pending"
        binding.symbolName.text = model?.Symbol_Name
        binding.symbolPrice.text = if(model?.LTP.isNullOrBlank()){"-"}else{model?.LTP.toString()}
        binding.symbolQuantity.text = java.lang.String.format(resources.getString(R.string.text_work_quantity), model?.WorkQty)
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
        initObservers();
    }
    private fun initObservers() {
        if(isObserversInitialized){
            return
        }
        isObserversInitialized = true
        orderPendingDetailsViewModel.mState
            .flowWithLifecycle(lifecycle,  Lifecycle.State.STARTED)
            .onEach {
                    it -> processResponse(it)
            }
            .launchIn(lifecycleScope)
    }
    private fun processResponse(state: PendingOrderDetailsViewState) {
        when(state){
            is PendingOrderDetailsViewState.CancelOrderSuccessResponse -> handleCancelOrderSuccessResponse()
            is PendingOrderDetailsViewState.ModifyOrderSuccessResponse -> handleModifyOrderSuccessResponse()
            is PendingOrderDetailsViewState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    private fun handleCancelOrderSuccessResponse() {
        orderPendingDetailsViewModel.resetStateToDefault()
        findNavController().navigate(R.id.ordersFragment)
    }

    private fun handleModifyOrderSuccessResponse() {
        orderPendingDetailsViewModel.resetStateToDefault()
        findNavController().navigate(R.id.ordersFragment)
    }

    private fun handleLoading(isLoading: Boolean) {
        if(isLoading){
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
}
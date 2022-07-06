package com.finsol.tech.presentation.portfolio

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.finsol.tech.FinsolApplication
import com.finsol.tech.R
import com.finsol.tech.data.model.*
import com.finsol.tech.databinding.FragmentPortfolioBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.portfolio.adapter.PortfolioAdapter
import com.finsol.tech.rabbitmq.MySingletonViewModel
import com.finsol.tech.util.AppConstants
import com.finsol.tech.util.PreferenceHelper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.lang.Math.abs
import java.util.*
import java.util.HashMap

class PortfolioFragment: BaseFragment(){
    private lateinit var portfolioList: List<PortfolioData>
    private lateinit var binding: FragmentPortfolioBinding
    private lateinit var mySingletonViewModel: MySingletonViewModel
    private lateinit var allContractsResponse: GetAllContractsResponse
    private lateinit var portfolioAdapter: PortfolioAdapter
    private var exchangeMap: HashMap<String, String> = HashMap()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var portfolioViewModel: PortfolioViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                   //navigate to watchlist fragment.
                    findNavController().navigate(R.id.watchListFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()
        exchangeMap = preferenceHelper.loadMap(context, AppConstants.KEY_PREF_EXCHANGE_MAP)
        binding = FragmentPortfolioBinding.inflate(inflater, container, false)
        portfolioViewModel = ViewModelProvider(requireActivity()).get(PortfolioViewModel::class.java)
        mySingletonViewModel = MySingletonViewModel.getMyViewModel(this)
        binding.toolbar.subTitle.text = preferenceHelper.getString(context, AppConstants.KEY_PREF_NAME, "")

        progressDialog = ProgressDialog(
            context,
            R.style.AppTheme_Dark_Dialog
        )
        progressDialog.isIndeterminate = true

        binding.toolbar.title.visibility = View.VISIBLE
        binding.toolbar.subTitle.visibility = View.VISIBLE
//        binding.toolbar.profilePic.visibility = View.VISIBLE

        portfolioViewModel.requestPortfolioDetails(preferenceHelper.getString(context, AppConstants.KEY_PREF_USER_ID, ""))

        // this creates a vertical layout Manager
        binding.portfolioRecyclerView.layoutManager = LinearLayoutManager(context)

        // This will pass the ArrayList to our Adapter
        portfolioAdapter = PortfolioAdapter(context)
        portfolioAdapter.setOnItemClickListener(object: PortfolioAdapter.ClickListener {
            override fun onItemClick(model: PortfolioData) {
                setTickAndLotData(model)
                val bundle = Bundle()
                bundle.putParcelable("selectedModel", model.toNonNullModel())
                findNavController().navigate(R.id.to_portfolioPartialDetailsFragment, bundle)
            }
        })

        // Setting the Adapter with the recyclerview
        binding.portfolioRecyclerView.adapter = portfolioAdapter

        binding.searchETNew.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {}
            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.isNotEmpty()){
                    filter(s.toString())
                }else {
                    portfolioAdapter.updateList(portfolioList)
                }
            }
        })


        return binding.root
    }
    private fun setTickAndLotData(model: PortfolioData?) {
        exchangeMap = preferenceHelper.loadMap(context, AppConstants.KEY_PREF_EXCHANGE_MAP)
        allContractsResponse =
            (requireActivity().application as FinsolApplication).getAllContracts()
        allContractsResponse.allContracts = allContractsResponse.allContracts +
                allContractsResponse.watchlist1 +
                allContractsResponse.watchlist2 +
                allContractsResponse.watchlist3
        val contract = allContractsResponse.allContracts.find {
            it.securityID == model?.securityID.toString()
        }
        model?.tickSize = contract?.tickSize.toString()
        model?.lotSize = contract?.lotSize.toString()
        model?.exchangeNameString = exchangeMap.get(model?.exchangeNameString.toString()).toString()
    }
    private fun filter(text: String) {

        if(text.isEmpty()){
            portfolioAdapter.updateList(portfolioList)
            return
        }

        val filteredlist: ArrayList<PortfolioData> = ArrayList()

        for (item in portfolioList) {
            if (item.productSymbol.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))) {
                filteredlist.add(item)
            }
        }
        portfolioAdapter.updateList(filteredlist)
        if (filteredlist.isEmpty()) {
            Toast.makeText(context, "No Data Found..", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers();
    }


    private fun updateListWithMarketData(hashMap: HashMap<String, Market>) {
        if(::portfolioList.isInitialized) {
            this.portfolioList?.forEach { portfolioModel ->
                val securityID = portfolioModel.securityID.toString()
                val markertData = hashMap[portfolioModel.securityID.toString()]
                if (securityID.equals(markertData?.securityID, true)) {
                    portfolioModel.LTP = if(markertData?.LTP.isNullOrBlank()){"-"}else{markertData?.LTP.toString()}
                    portfolioModel.closePrice = markertData?.ClosePrice?.toFloat() ?: 0f
                    setTickAndLotData(portfolioModel)
                    calcIntradayPnl(portfolioModel)
                }
                if(!portfolioModel.LTP.isNullOrBlank()) {
                    val change = portfolioModel.LTP.toFloat() - portfolioModel.closePrice
                    val changePercent: Float
                    if (portfolioModel.closePrice != 0f) {
                        changePercent = ((change / portfolioModel.closePrice) * 100).toFloat()
                    } else {
                        changePercent = ((change) * 100).toFloat()
                    }
                    portfolioModel.LTPChangePercent = java.lang.String.format(
                        resources.getString(R.string.text_cumulative_pnl),
                        changePercent
                    )
                } else {
                    portfolioModel.LTPChangePercent = "(-)"
                }
            }
            doTotalCalc(portfolioList)
            //portfolioAdapter.updateList(portfolioList)
            filter(binding.searchETNew.text.toString())
        }

    }
    private fun calcIntradayPnl(portfolioModel: PortfolioData) {
//        IntrradayPNL = LotSize * ((Math.Abs(AvgSellPrice * TotalQtySell))
//        - (AvgBuyPrice * TotalQtyBuy)
//        + ((TotalQtyBuy + TotalQtySell)* LTP))

//        CumulativePNL = LotSize * ((Math.Abs(AvgSellPrice * TotalQtySell))
//        - (AvgBuyPrice * TotalQtyBuy)
//        - (OpeningQty * CloseingPrice)
//        + (NetPosition * LTP))

        val a = portfolioModel.lotSize.toDouble() * Math.abs(portfolioModel.totalQtySell*portfolioModel.avgSellPrice)
        val b = portfolioModel.avgBuyPrice*portfolioModel.totalQtyBuy
        val c = ((portfolioModel.totalQtyBuy+portfolioModel.totalQtySell)*portfolioModel.LTP.toDouble())
        portfolioModel.intrradayPNL = (a-b+c).toInt()

        val d = portfolioModel.openingQty*portfolioModel.closeingPrice
        val e = portfolioModel.netPosition*portfolioModel.LTP.toDouble()
        portfolioModel.cumulativePNL = (a-b-d+e).toDouble()


    }
    private fun initObservers() {
        portfolioViewModel.mState
            .flowWithLifecycle(lifecycle,  Lifecycle.State.STARTED)
            .onEach {
                    it -> processResponse(it)
            }
            .launchIn(lifecycleScope)
    }

    private fun processResponse(state: PortfolioViewState) {
        when(state){
            is PortfolioViewState.PortfolioSuccessResponse -> handlePortfolioSuccessResponse(state.portfolioResponse)
            is PortfolioViewState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    private fun handlePortfolioSuccessResponse(portfolioResponse: PortfolioResponse) {
        portfolioList = portfolioResponse.GetPortfolioResult.toList()
        portfolioViewModel.resetStateToDefault()
        portfolioList = portfolioResponse.GetPortfolioResult.toList()
        doTotalCalc(portfolioResponse.GetPortfolioResult.toList())

        allContractsResponse =
            (requireActivity().application as FinsolApplication).getAllContracts()
        allContractsResponse.allContracts = allContractsResponse.allContracts +
                allContractsResponse.watchlist1 +
                allContractsResponse.watchlist2 +
                allContractsResponse.watchlist3
        portfolioList.forEach { portfolioModel ->
            allContractsResponse.allContracts.forEach {
                if(it.securityID == portfolioModel.securityID.toString()){
                    portfolioModel.LTP = it.lTP.toString()
                    portfolioModel.maturityDay = it.maturityDay
                }
            }
        }

//        portfolioAdapter.updateList(portfolioResponse.GetPortfolioResult.toList())
        filter(binding.searchETNew.text.toString())
        mySingletonViewModel.getMarketData()?.observe(viewLifecycleOwner){
            updateListWithMarketData(it)
        }
    }

    private fun doTotalCalc(list: List<PortfolioData>) {
        var totalCumulativePNL:Double = 0.0
        var totalIntrradayPNL:Double = 0.0
        var totalInvested:Double = 0.0
        if(list.isNotEmpty()) {
            list.map {
                totalCumulativePNL += it.cumulativePNL
                totalIntrradayPNL += it.intrradayPNL

                val avg:Double = if(it.netPosition > 0){
                    it.avgBuyPrice
                } else {
                    it.avgSellPrice
                }
                val invested = abs(it.netPosition)*avg
                totalInvested += invested
            }
        }
        if(totalCumulativePNL < 0.0){
            context?.let {
                binding.totalCumulativeValue.setTextColor(ContextCompat.getColor(it,(R.color.red)))
                binding.topLabel2.text = it.resources?.getString(R.string.text_total_loss)
            }
        }else{
            context?.let {
                binding.totalCumulativeValue.setTextColor(ContextCompat.getColor(it,(R.color.green)))
                binding.topLabel2.text = it.resources?.getString(R.string.text_total_profit)
            }
        }
        if(totalIntrradayPNL < 0.0){
            context?.let {
                binding.intrradayValue.setTextColor(ContextCompat.getColor(it,(R.color.red)))
            }
        }else{
            context?.let {
                binding.intrradayValue.setTextColor(ContextCompat.getColor(it,(R.color.green)))
            }
        }
        val cumilativePercentage = ((totalCumulativePNL/totalInvested)*100)
        val intrradayPercentage = ((totalIntrradayPNL/totalInvested)*100)
        binding.intrradayValue.text = java.lang.String.format(
            context?.resources?.getString(R.string.text_cumulative_pnl),
            totalIntrradayPNL
        )+"    "+java.lang.String.format(context?.resources?.getString(R.string.text_cumulative_pnl), intrradayPercentage)+"%"
        binding.totalCumulativeValue.text = java.lang.String.format(
            context?.resources?.getString(R.string.text_cumulative_pnl),
            totalCumulativePNL
            )+" ("+java.lang.String.format(context?.resources?.getString(R.string.text_cumulative_pnl), cumilativePercentage)+"%)"
        binding.totalInvestedAmount.text = java.lang.String.format(context?.resources?.getString(R.string.text_cumulative_pnl), totalInvested)
    }

    private fun handleLoading(isLoading: Boolean) {
        if(isLoading){
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }
}
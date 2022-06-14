package com.finsol.tech.presentation.portfolio

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.finsol.tech.R
import com.finsol.tech.data.model.Market
import com.finsol.tech.data.model.PortfolioData
import com.finsol.tech.data.model.PortfolioResponse
import com.finsol.tech.data.model.toNonNullModel
import com.finsol.tech.databinding.FragmentPortfolioBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.portfolio.adapter.PortfolioAdapter
import com.finsol.tech.rabbitmq.MySingletonViewModel
import com.finsol.tech.util.AppConstants
import com.finsol.tech.util.PreferenceHelper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import java.util.HashMap

class PortfolioFragment: BaseFragment(){
    private lateinit var portfolioList: List<PortfolioData>
    private lateinit var binding: FragmentPortfolioBinding
    private lateinit var mySingletonViewModel: MySingletonViewModel
    private lateinit var portfolioAdapter: PortfolioAdapter
    private lateinit var progressDialog: ProgressDialog
    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var portfolioViewModel: PortfolioViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()
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
        binding.toolbar.profilePic.visibility = View.VISIBLE

        portfolioViewModel.requestPortfolioDetails(preferenceHelper.getString(context, AppConstants.KEY_PREF_USER_ID, ""))
        mySingletonViewModel.getMarketData()?.observe(viewLifecycleOwner){
            updateListWithMarketData(it)
        }
        // this creates a vertical layout Manager
        binding.portfolioRecyclerView.layoutManager = LinearLayoutManager(context)

        // This will pass the ArrayList to our Adapter
        portfolioAdapter = PortfolioAdapter(context)
        portfolioAdapter.setOnItemClickListener(object: PortfolioAdapter.ClickListener {
            override fun onItemClick(model: PortfolioData) {
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

    private fun filter(text: String) {
        val filteredlist: ArrayList<PortfolioData> = ArrayList()

        for (item in portfolioList) {
            if (item.productSymbol.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(context, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            portfolioAdapter.updateList(filteredlist)
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
                }
            }
            portfolioAdapter.updateList(portfolioList)
        }

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
        portfolioAdapter.updateList(portfolioResponse.GetPortfolioResult.toList())
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
                val invested = it.netPosition*avg
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
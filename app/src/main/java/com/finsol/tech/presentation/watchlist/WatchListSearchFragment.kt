package com.finsol.tech.presentation.watchlist

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.finsol.tech.FinsolApplication
import com.finsol.tech.R
import com.finsol.tech.data.model.Contracts
import com.finsol.tech.data.model.GenericMessageResponse
import com.finsol.tech.data.model.GetAllContractsResponse
import com.finsol.tech.data.model.PendingOrderModel
import com.finsol.tech.databinding.FragmentWatchlistSearchBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.watchlist.adapter.WatchListSearchAdapter
import com.finsol.tech.util.AppConstants
import com.finsol.tech.util.PreferenceHelper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*


class WatchListSearchFragment : BaseFragment() {
    private lateinit var list: List<Contracts>

    private lateinit var watchListSearchViewModel: WatchListSearchViewModel
    private lateinit var binding: FragmentWatchlistSearchBinding
    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var progressDialog: ProgressDialog
    private lateinit var adapter: WatchListSearchAdapter
    private lateinit var selectedModel: Contracts
    private var isViewCreated = false;
    private var currentWatchListSize: Int = 1
    private var currentWatchListNumber: Int = 1

    private lateinit var allContractsResponse: GetAllContractsResponse
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers();
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWatchlistSearchBinding.inflate(inflater, container, false)
        watchListSearchViewModel =
            ViewModelProvider(requireActivity()).get(WatchListSearchViewModel::class.java)
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()
        progressDialog = ProgressDialog(
            context,
            R.style.AppTheme_Dark_Dialog
        )
        progressDialog.isIndeterminate = true
        allContractsResponse =
            (requireActivity().application as FinsolApplication).getAllContracts()!!
        currentWatchListNumber = arguments?.getInt("watchListNumber")!!

        updateCurrentSizeAndList()

        binding.count.text = currentWatchListSize.toString() + "/40"

        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.searchET.visibility = View.VISIBLE

        binding.toolbar.searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {}
            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                filter(s.toString())
            }
        })

        binding.textWatchListNumber.text = java.lang.String.format(
            resources.getString(R.string.text_addToWatchList),
            currentWatchListNumber
        )
        binding.toolbar.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        // this creates a vertical layout Manager
        binding.watchListRecyclerView.layoutManager = LinearLayoutManager(context)

        adapter = WatchListSearchAdapter()

//        list = allContractsResponse.allContracts.sortedBy { it.displayName }
        adapter.updateList(list)
        adapter.setOnItemClickListener(object : WatchListSearchAdapter.ClickListener {
            override fun onItemClick(model: Contracts) {
                val userID = preferenceHelper.getString(context, AppConstants.KEY_PREF_USER_ID, "")
                selectedModel = model
                progressDialog.setMessage(getString(R.string.text_please_wait))
                if (currentWatchListSize < 40) {
                    watchListSearchViewModel.addToWatchList(
                        userID,
                        currentWatchListNumber.toString(),
                        model.securityID.toString()
                    )
                } else {
                    Toast.makeText(
                        context,
                        "Only 40 items allowed for each watch list",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        // Setting the Adapter with the recyclerview
        binding.watchListRecyclerView.adapter = adapter

        isViewCreated = true;
        return binding.root
    }

    private fun filter(text: String) {

        if(text.isEmpty()){
            adapter.updateList(list)
            return
        }

        val filteredlist: ArrayList<Contracts> = ArrayList()

        for (item in list) {
            if (item.symbolName.lowercase(Locale.getDefault()).startsWith(text.lowercase(Locale.getDefault()))) {
                filteredlist.add(item)
            }
            if (item.exchangeName.lowercase(Locale.getDefault()).startsWith(text.lowercase(Locale.getDefault()))) {
                filteredlist.add(item)
            }
            if (item.expiryString.lowercase(Locale.getDefault()).startsWith(text.lowercase(Locale.getDefault()))) {
                filteredlist.add(item)
            }

        }

        adapter.updateList(filteredlist)
        if (filteredlist.isEmpty()) {
            Toast.makeText(context, "No Data Found..", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateCurrentSizeAndList() {
        currentWatchListNumber.let {
            currentWatchListSize = when (it) {
                1 -> allContractsResponse.watchlist1.size
                2 -> allContractsResponse.watchlist2.size
                3 -> allContractsResponse.watchlist3.size
                else -> 0
            }
        }

        currentWatchListNumber.let {
            when (it) {
                1 -> {
                    allContractsResponse.watchlist1.map {
                        it.isAddedToWatchList = true
                    }
                    list =
                        allContractsResponse.allContracts + allContractsResponse.watchlist1
                }
                2 -> {
                    allContractsResponse.watchlist2.map {
                        it.isAddedToWatchList = true
                    }
                   list =
                        allContractsResponse.allContracts + allContractsResponse.watchlist2
                }
                3 -> {
                    allContractsResponse.watchlist3.map {
                        it.isAddedToWatchList = true
                    }
                    list =
                        allContractsResponse.allContracts + allContractsResponse.watchlist3
                }
                else -> {
                }
            }
        }
        list = list.sortedBy { it.displayName }
    }

    private fun updateAllContractsList(model: Contracts) {
        val mutableList = allContractsResponse.allContracts.toMutableList()
        Log.i("after adding::",""+mutableList.remove(model))
        allContractsResponse.allContracts = mutableList
        currentWatchListNumber.let {
            when (it) {
                1 -> {
                    allContractsResponse.watchlist1 =  allContractsResponse.watchlist1 + model
                }
                2 -> {
                    allContractsResponse.watchlist2 =  allContractsResponse.watchlist2 + model
                }
                3 -> {
                    allContractsResponse.watchlist3 =  allContractsResponse.watchlist3 + model
                }
                else -> {
                }
            }
        }
        updateCurrentSizeAndList()
        if (isViewCreated) {
            val list2:List<Contracts> = list.sortedBy { it.displayName }
            adapter.updateList(list2)
        }
    }

    private fun initObservers() {
        watchListSearchViewModel.mState
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { it ->
                processResponse(it)
            }
            .launchIn(lifecycleScope)
    }

    private fun processResponse(state: WatchListSearchViewState) {
        when (state) {
            is WatchListSearchViewState.AddToWatchListSuccessResponse -> handleSuccessResponse(state.genericMessageResponse)
            is WatchListSearchViewState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    private fun handleSuccessResponse(genericMessageResponse: GenericMessageResponse) {
        watchListSearchViewModel.resetStateToDefault()
        updateAllContractsList(selectedModel)
        if (currentWatchListSize < 41) {
            binding.count.text = currentWatchListSize.toString() + "/40"
            Toast.makeText(
                context,
                "added to watch list::" + genericMessageResponse.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        if (isLoading) {
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }
}
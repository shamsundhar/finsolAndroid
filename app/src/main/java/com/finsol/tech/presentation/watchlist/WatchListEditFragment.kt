package com.finsol.tech.presentation.watchlist

import android.app.ProgressDialog
import android.os.Bundle
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
import com.finsol.tech.databinding.FragmentWatchlistEditBinding
import com.finsol.tech.databinding.FragmentWatchlistSearchBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.watchlist.adapter.WatchListEditAdapter
import com.finsol.tech.presentation.watchlist.adapter.WatchListSearchAdapter
import com.finsol.tech.util.AppConstants
import com.finsol.tech.util.PreferenceHelper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class WatchListEditFragment : BaseFragment() {
    private lateinit var watchListEditViewModel: WatchListEditViewModel
    private lateinit var binding: FragmentWatchlistEditBinding
    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var progressDialog: ProgressDialog
    private lateinit var adapter:WatchListEditAdapter
    private lateinit var list:List<Contracts>
    private var isViewCreated = false;
    private var currentWatchListSize: Int = 1

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
        binding = FragmentWatchlistEditBinding.inflate(inflater, container, false)
        watchListEditViewModel = ViewModelProvider(requireActivity()).get(WatchListEditViewModel::class.java)
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()
        progressDialog = ProgressDialog(
            context,
            R.style.AppTheme_Dark_Dialog
        )
        progressDialog.isIndeterminate = true
        allContractsResponse =
            (requireActivity().application as FinsolApplication).getAllContracts()
        val watchListNumber = arguments?.getInt("watchListNumber")

        watchListNumber.let {
            currentWatchListSize = when (it) {
                1 -> allContractsResponse.watchlist1.size
                2 -> allContractsResponse.watchlist2.size
                3 -> allContractsResponse.watchlist3.size
                else -> 0
            }
        }

        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.title2.visibility = View.VISIBLE

        binding.textWatchListNumber.text = java.lang.String.format(resources.getString(R.string.text_removeFromWatchList), watchListNumber)
        binding.toolbar.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        // this creates a vertical layout Manager
        binding.watchListRecyclerView.layoutManager = LinearLayoutManager(context)

        adapter = WatchListEditAdapter()

        allContractsResponse.watchlist1.map {
            it.isAddedToWatchList = true
        }
        allContractsResponse.allContracts = allContractsResponse.allContracts + allContractsResponse.watchlist1
        // TODO sort allContracts based on displayName(A-Z)
        allContractsResponse.allContracts.sortedBy { it.displayName }

        watchListNumber.let {
            list = when(it) {
                1 -> allContractsResponse.watchlist1
                2 -> allContractsResponse.watchlist2
                3 -> allContractsResponse.watchlist3
                else -> allContractsResponse.watchlist1
            }
        }

        adapter.updateList(list)
        adapter.setOnItemClickListener(object : WatchListEditAdapter.ClickListener {
            override fun onItemClick(model: Contracts) {
                val userID = preferenceHelper.getString(context, AppConstants.KEY_PREF_USER_ID, "")
                progressDialog.setMessage(getString(R.string.text_please_wait))
                if(currentWatchListSize < 40) {
                    updateAllContractsList(model)
                    watchListEditViewModel.removeFromWatchList(
                        userID,
                        watchListNumber.toString(),
                        model.securityID.toString()
                    )
                } else {
                    Toast.makeText(context, "Only 40 items allowed for each watch list", Toast.LENGTH_SHORT).show()
                }
            }
        })

        // Setting the Adapter with the recyclerview
        binding.watchListRecyclerView.adapter = adapter

        isViewCreated = true;
        return binding.root
    }
    private fun updateAllContractsList(model: Contracts) {
       val mutableList = list.toMutableList()
        mutableList.remove(model)
        list = mutableList
        if(isViewCreated){
            adapter.updateList(list)
        }
    }
    private fun initObservers() {
        watchListEditViewModel.mState
            .flowWithLifecycle(lifecycle,  Lifecycle.State.STARTED)
            .onEach {
                    it -> processResponse(it)
            }
            .launchIn(lifecycleScope)
    }

    private fun processResponse(state: WatchListEditViewState) {
        when(state){
            is WatchListEditViewState.RemoveFromWatchListSuccessResponse -> handleSuccessResponse(state.genericMessageResponse)
            is WatchListEditViewState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    private fun handleSuccessResponse(genericMessageResponse: GenericMessageResponse) {
        currentWatchListSize++
        if(currentWatchListSize < 41) {
            Toast.makeText(
                context,
                "remove from watch list::" + genericMessageResponse.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        if(isLoading){
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }
}
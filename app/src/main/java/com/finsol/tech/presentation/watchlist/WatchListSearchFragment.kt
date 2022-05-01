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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.finsol.tech.FinsolApplication
import com.finsol.tech.R
import com.finsol.tech.data.model.Contracts
import com.finsol.tech.data.model.GenericMessageResponse
import com.finsol.tech.data.model.GetAllContractsResponse
import com.finsol.tech.data.model.PendingOrderModel
import com.finsol.tech.databinding.FragmentWatchlistSearchBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.prelogin.LoginMarketViewState
import com.finsol.tech.presentation.prelogin.LoginViewModel
import com.finsol.tech.presentation.watchlist.adapter.WatchListSearchAdapter
import com.finsol.tech.util.AppConstants
import com.finsol.tech.util.PreferenceHelper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class WatchListSearchFragment : BaseFragment() {
    private lateinit var watchListSearchViewModel: WatchListSearchViewModel
    private lateinit var binding: FragmentWatchlistSearchBinding
    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var progressDialog: ProgressDialog
    private lateinit var allContractsResponse: GetAllContractsResponse
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers();
    }

    private fun initObservers() {
        watchListSearchViewModel.mState
            .flowWithLifecycle(lifecycle,  Lifecycle.State.STARTED)
            .onEach {
                    it -> processResponse(it)
            }
            .launchIn(lifecycleScope)
    }

    private fun processResponse(state: WatchListSearchViewState) {
        when(state){
            is WatchListSearchViewState.AddToWatchListSuccessResponse -> handleSuccessResponse(state.genericMessageResponse)
            is WatchListSearchViewState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    private fun handleSuccessResponse(genericMessageResponse: GenericMessageResponse) {
        Toast.makeText(context, "added to watch list::"+genericMessageResponse.response, Toast.LENGTH_SHORT).show()
    }

    private fun handleLoading(isLoading: Boolean) {
        if(isLoading){
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWatchlistSearchBinding.inflate(inflater, container, false)
        watchListSearchViewModel = ViewModelProvider(requireActivity()).get(WatchListSearchViewModel::class.java)
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()
        progressDialog = ProgressDialog(
            context,
            R.style.AppTheme_Dark_Dialog
        )
        progressDialog.isIndeterminate = true
        allContractsResponse =
            (requireActivity().application as FinsolApplication).getAllContracts()
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.searchET.visibility = View.VISIBLE

        binding.toolbar.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        // this creates a vertical layout Manager
        binding.watchListRecyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = WatchListSearchAdapter()

        allContractsResponse.watchlist1.map {
            it.isAddedToWatchList = true
        }
        allContractsResponse.allContracts = allContractsResponse.allContracts + allContractsResponse.watchlist1
        // TODO sort allContracts based on displayName(A-Z)
        allContractsResponse.allContracts.sortedBy { it.displayName }
        adapter.updateList(allContractsResponse.allContracts)
        adapter.setOnItemClickListener(object : WatchListSearchAdapter.ClickListener {
            override fun onItemClick(model: Contracts) {
                val userID = preferenceHelper.getString(context, AppConstants.KEY_PREF_USER_ID, "")
                progressDialog.setMessage(getString(R.string.text_please_wait))
                watchListSearchViewModel.addToWatchList(userID, "1", model.securityID.toString())
            }
        })

        // Setting the Adapter with the recyclerview
        binding.watchListRecyclerView.adapter = adapter


        return binding.root
    }
}
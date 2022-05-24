package com.finsol.tech.presentation.portfolio

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.finsol.tech.R
import com.finsol.tech.data.model.PortfolioData
import com.finsol.tech.data.model.PortfolioResponse
import com.finsol.tech.databinding.FragmentPortfolioBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.portfolio.adapter.PortfolioAdapter
import com.finsol.tech.util.AppConstants
import com.finsol.tech.util.PreferenceHelper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PortfolioFragment: BaseFragment(){
    private lateinit var binding: FragmentPortfolioBinding
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

        // this creates a vertical layout Manager
        binding.portfolioRecyclerView.layoutManager = LinearLayoutManager(context)

        // This will pass the ArrayList to our Adapter
        portfolioAdapter = PortfolioAdapter()
        portfolioAdapter.setOnItemClickListener(object: PortfolioAdapter.ClickListener {
            override fun onItemClick(model: PortfolioData) {
                val bundle = Bundle()
                bundle.putParcelable("selectedModel", model)
                findNavController().navigate(R.id.to_portfolioPartialDetailsFragment, bundle)
            }
        })

        // Setting the Adapter with the recyclerview
        binding.portfolioRecyclerView.adapter = portfolioAdapter


        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers();
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
        portfolioAdapter.updateList(portfolioResponse.GetPortfolioResult.toList())
    }
    private fun handleLoading(isLoading: Boolean) {
        if(isLoading){
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }
}
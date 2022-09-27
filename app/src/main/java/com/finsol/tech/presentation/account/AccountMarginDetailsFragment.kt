package com.finsol.tech.presentation.account

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
import com.finsol.tech.data.model.GenericMessageResponse
import com.finsol.tech.databinding.FragmentAccountMarginDetailsBinding
import com.finsol.tech.domain.ctcl.CTCLDetails
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.orders.OrdersViewModel
import com.finsol.tech.presentation.prelogin.RegisterViewState
import com.finsol.tech.rabbitmq.MySingletonViewModel
import com.finsol.tech.util.AppConstants
import com.finsol.tech.util.PreferenceHelper
import com.finsol.tech.util.Utilities
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AccountMarginDetailsFragment: BaseFragment() {
    private lateinit var binding: FragmentAccountMarginDetailsBinding
    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var accountFundsViewModel:AccountFundsViewModel
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mySingletonViewModel: MySingletonViewModel

    private var isObserversInitialized : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountMarginDetailsBinding.inflate(inflater, container, false)
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.title2.visibility = View.VISIBLE
        binding.toolbar.title2.text = getString(R.string.text_margin_details)

        progressDialog = ProgressDialog(
            context,
            R.style.AppTheme_Dark_Dialog
        )
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(getString(R.string.text_please_wait))

        accountFundsViewModel = ViewModelProvider(requireActivity()).get(AccountFundsViewModel::class.java)
        mySingletonViewModel = MySingletonViewModel.getMyViewModel(this)

        binding.toolbar.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
//        binding.addFundsButton.setOnClickListener {
//            accountFundsViewModel.addFunds(
//                preferenceHelper.getString(context, AppConstants.KEY_PREF_USER_NAME, "")
//            )
//        }
//        binding.withdrawButton.setOnClickListener {
//            accountFundsViewModel.withdrawFunds(
//                preferenceHelper.getString(context, AppConstants.KEY_PREF_USER_NAME, "")
//            )
//        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers();
    }
    private fun initObservers() {
        if (isObserversInitialized) {
            return
        }
        isObserversInitialized = true
        accountFundsViewModel.mState
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { it ->
                processResponse(it)
            }
            .launchIn(lifecycleScope)

        mySingletonViewModel.getUserCTCLData().observe(viewLifecycleOwner){
            it?.let {
                updateDetails(it)
            }
        }

    }

    private fun updateDetails(it: CTCLDetails) {
        binding.accountValue.text = ""
        binding.exchangeValue.text = it.ExchangeName
        binding.openBalanceValue.text = it.OpenClientBalance
        binding.utilizedMarginValue.text = it.UtilisedMargin
        binding.availableMarginValue.text = it.AvailableMargin
        binding.intradayPnlValue.text = it.TotalIntradayPnL
        binding.totalPnLValue.text = it.TotalPnL
    }

    private fun processResponse(state: FundsViewState) {
        when (state) {
            is FundsViewState.AddFundsSuccessResponse -> handleAddFundsSuccessResponse(state.genericMessageResponse)
            is FundsViewState.WithdrawFundsSuccessResponse -> handleWithdrawFundsSuccessResponse(state.genericMessageResponse)
            is FundsViewState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    private fun handleAddFundsSuccessResponse(genericMessageResponse: GenericMessageResponse) {
        Utilities.showDialogWithOneButton(context, genericMessageResponse.message) {
            accountFundsViewModel.resetStateToDefault()
            findNavController().popBackStack()
        }
    }
    private fun handleWithdrawFundsSuccessResponse(genericMessageResponse: GenericMessageResponse) {
        Utilities.showDialogWithOneButton(context, genericMessageResponse.message) {
            accountFundsViewModel.resetStateToDefault()
            findNavController().popBackStack()
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
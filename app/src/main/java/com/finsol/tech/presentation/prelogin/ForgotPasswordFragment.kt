package com.finsol.tech.presentation.prelogin

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
import com.finsol.tech.databinding.FragmentForgotPasswordBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.orders.OrdersViewModel
import com.finsol.tech.util.PreferenceHelper
import com.finsol.tech.util.Utilities
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ForgotPasswordFragment: BaseFragment() {
    private lateinit var binding: FragmentForgotPasswordBinding
    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel
    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var progressDialog: ProgressDialog
    private var isObserversInitialized: Boolean = false
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
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()
        progressDialog = ProgressDialog(
            context,
            R.style.AppTheme_Dark_Dialog
        )
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(getString(R.string.text_please_wait))

        forgotPasswordViewModel = ViewModelProvider(requireActivity()).get(ForgotPasswordViewModel::class.java)

        binding.sendBtn.setOnClickListener {
            sendButtonClicked()
        }

        return binding.root
    }
    private fun sendButtonClicked() {
        if(validate()){
            forgotPasswordViewModel.forgotPasword(
                binding.email.toString()
            )
        }
    }
    private fun validate():Boolean {
        var result = false
        val _email: String = binding.email.text.toString().trim()
        if (_email.length > 0) {
            binding.email.setError(null)
            if (Utilities.isValidEmail(_email)) {
                result = true
            } else {
                Utilities.showDialogWithOneButton(
                    context,
                    "Enter valid email.",
                    null
                )
            }
        } else {
            binding.email.setError("Field should not be empty")
        }
        return result
    }
    private fun initObservers() {
        if (isObserversInitialized) {
            return
        }
        isObserversInitialized = true
        forgotPasswordViewModel.mState
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { it ->
                processResponse(it)
            }
            .launchIn(lifecycleScope)
    }

    private fun processResponse(state: ForgotPasswordViewState) {
        when (state) {
            is ForgotPasswordViewState.SuccessResponse -> handleSuccessResponse(state.genericMessageResponse)
            is ForgotPasswordViewState.IsLoading -> handleLoading(state.isLoading)
            is ForgotPasswordViewState.ShowToast -> handleToast(state.message)
        }
    }
    private fun handleSuccessResponse(genericMessageResponse: GenericMessageResponse) {
        Utilities.showDialogWithOneButton(context, genericMessageResponse.message) {
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
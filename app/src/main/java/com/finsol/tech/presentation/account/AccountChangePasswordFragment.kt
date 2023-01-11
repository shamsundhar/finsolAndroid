package com.finsol.tech.presentation.account

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.finsol.tech.R
import com.finsol.tech.data.model.GenericMessageResponse
import com.finsol.tech.databinding.FragmentAccountChangePasswordBinding
import com.finsol.tech.databinding.FragmentLoginBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.prelogin.LoginMarketViewState
import com.finsol.tech.presentation.prelogin.LoginViewModel
import com.finsol.tech.util.AppConstants
import com.finsol.tech.util.PreferenceHelper
import com.finsol.tech.util.Utilities
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AccountChangePasswordFragment: BaseFragment() {
    private lateinit var binding: FragmentAccountChangePasswordBinding
    private lateinit var accountChangePasswordViewModel: AccountChangePasswordViewModel
    private lateinit var progressDialog: ProgressDialog
    private lateinit var preferenceHelper: PreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountChangePasswordBinding.inflate(inflater, container, false)
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()
        accountChangePasswordViewModel = ViewModelProvider(requireActivity()).get(AccountChangePasswordViewModel::class.java)

        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.title2.visibility = View.VISIBLE

        progressDialog = ProgressDialog(
            context,
            R.style.AppTheme_Dark_Dialog
        )
        progressDialog.isIndeterminate = true

        binding.toolbar.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.changePasswordButton.setOnClickListener {
            changePasswordClicked()
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers();
    }

    private fun changePasswordClicked() {
        if(validate()){
            accountChangePasswordViewModel.requestChangePassword(preferenceHelper.getString(context, AppConstants.KEY_PREF_USER_ID, ""),
                preferenceHelper.getString(context, AppConstants.KEY_PREF_USER_NAME, ""),
                binding.userConfPassword.text.toString().trim())
        }
    }
    private fun validate(): Boolean {
        var result = false
        val _password = binding.userPassword.text.toString().trim()
        val _confPassword = binding.userConfPassword.text.toString().trim()
        if(_password.length > 0){
            binding.userPassword.setError(null)
            if(_confPassword.length > 0){
                binding.userConfPassword.setError(null)
                if(_password.equals(_confPassword)){
                   if(Utilities.isValidPassword(_password)){
                       result = true
                   } else {
                       Utilities.showDialogWithOneButton(
                           context,
                           "Password should not contain special characters.",
                           null
                       )
                   }
                } else {
                    Utilities.showDialogWithOneButton(
                        context,
                        "Passwords are not same",
                        null
                    )
                }
            } else {
                binding.userConfPassword.setError("Field should not be empty")
            }
        } else {
            binding.userPassword.setError("Field should not be empty")
        }
        return result
    }

    private fun initObservers() {
        accountChangePasswordViewModel.mState
            .flowWithLifecycle(lifecycle,  Lifecycle.State.STARTED)
            .onEach {
                    it -> processResponse(it)
            }
            .launchIn(lifecycleScope)
    }
    private fun processResponse(state: ChangePasswordViewState) {
        when(state){
            is ChangePasswordViewState.SuccessResponse -> handleSuccessResponse(state.genericMessageResponse)
            is ChangePasswordViewState.IsLoading -> handleLoading(state.isLoading)
            is ChangePasswordViewState.ShowToast -> handleToast(state.message)
        }
    }
    private fun handleSuccessResponse(response: GenericMessageResponse) {
        Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
        accountChangePasswordViewModel.resetStateToDefault()
        findNavController().popBackStack()
    }
    private fun handleLoading(isLoading: Boolean) {
        if(isLoading){
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }
}
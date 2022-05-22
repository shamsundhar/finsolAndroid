package com.finsol.tech.presentation.prelogin

import android.app.ProgressDialog
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.finsol.tech.R
import com.finsol.tech.databinding.FragmentLoginBinding
import com.finsol.tech.domain.model.LoginResponseDomainModel
import com.finsol.tech.domain.model.ProfileResponseDomainModel
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.util.AppConstants.*
import com.finsol.tech.util.PreferenceHelper
import com.finsol.tech.util.Utilities
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class LoginFragment : BaseFragment() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: FragmentLoginBinding
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
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()
        loginViewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)


        /* TODO
            Test data login credentails need to be removed - START
         */
        binding.username.setText("SHYAM1")
        binding.password.setText("mobile")
        /*
           Test data login credentails need to be removed - END
        */

        progressDialog = ProgressDialog(
            context,
            R.style.AppTheme_Dark_Dialog
        )
        progressDialog.isIndeterminate = true


        binding.loginBtn.setOnClickListener {
            progressDialog.setMessage(getString(R.string.text_authenticating))
            if (validate()) {
                loginViewModel.requestLogin(
                    binding.username.text.toString(),
                    binding.password.text.toString()
                )
            }
        }
        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(R.id.to_forgotPasswordFragmentFromLogin)
        }
        binding.signUp.setOnClickListener {
            findNavController().navigate(R.id.to_registerFragmentFromLogin)
        }
        binding.showPassBtn.setOnClickListener {
            showHidePass(it)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers();
    }

    private fun validate():Boolean {
        var result:Boolean = false
        val username = binding.username.text.toString()
        val password = binding.password.text.toString()

        if(username.isNotBlank()){
            binding.username.error = null
            if(password.isNotBlank()){
                binding.password.error = null
                    result = true
            } else {
                binding.password.error = "Field should not be empty"
            }
        } else {
            binding.username.error = "Field should not be empty"
        }
        return result
    }

    private fun initObservers() {
        loginViewModel.mState
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { it ->
                processResponse(it)
            }
            .launchIn(lifecycleScope)
    }

    private fun processResponse(state: LoginMarketViewState) {
        when (state) {
            is LoginMarketViewState.SuccessResponse -> handleLoginSuccessResponse(state.loginResponseDomainModel)
            is LoginMarketViewState.IsLoading -> handleLoading(state.isLoading)
            is LoginMarketViewState.ProfileSuccessResponse -> handleProfileSuccessResponse(state.profileResponseDomainModel)
        }
    }


    private fun handleLoginSuccessResponse(loginResponseDomainModel: LoginResponseDomainModel) {
        loginViewModel.resetStateToDefault()
        if (loginResponseDomainModel.status) {
            preferenceHelper.setString(
                context,
                KEY_PREF_USER_ID,
                loginResponseDomainModel.userID.toString()
            )
            progressDialog.setMessage(getString(R.string.text_getting_details))
            loginViewModel.requestUserProfileDetails(loginResponseDomainModel.userID.toString())
        } else {
            Utilities.showDialogWithOneButton(
                context,
                loginResponseDomainModel.message,
                null
            )
        }
    }

//    private fun handlePendingOrdersResponse(pendingOrderResponse: Array<PendingOrderModel>) {
//        Toast.makeText(context, "pending orders::"+pendingOrderResponse.toList().get(0).AccountID, Toast.LENGTH_SHORT).show()
//    }

//    private fun handleOrderHistoryResponse(orderHistoryResponse: Array<OrderHistoryModel>) {
//        Toast.makeText(context, "order history::"+orderHistoryResponse.toList().get(0).AccountID, Toast.LENGTH_SHORT).show()
//    }

    private fun handleProfileSuccessResponse(profileResponseDomainModel: ProfileResponseDomainModel) {
        loginViewModel.resetStateToDefault()
        preferenceHelper.setString(context, KEY_PREF_NAME, profileResponseDomainModel.name)
        preferenceHelper.setString(context, KEY_PREF_EMAIL, profileResponseDomainModel.emailid)
        preferenceHelper.setString(context, KEY_PREF_PHONE, profileResponseDomainModel.phone)
        progressDialog.setMessage(getString(R.string.text_getting_details))
//        loginViewModel.getExchangeEnumData()
        findNavController().navigate(R.id.to_watchListFragmentFromLogin)

    }

//    private fun handleAllContractsSuccessResponse(allContractsResponse: GetAllContractsResponse) {
//
////        findNavController().navigate(R.id.to_watchListFragmentFromLogin)
//
//    }

    private fun handleLoading(isLoading: Boolean) {
        if (isLoading) {
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }

    fun showHidePass(view: View) {
        if (binding.password.transformationMethod == PasswordTransformationMethod.getInstance()) {
            (view as ImageView).setImageResource(R.drawable.ic_eye_off)

            //Show Password
            binding.password.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            (view as ImageView).setImageResource(R.drawable.ic_eye)

            //Hide Password
            binding.password.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }
}
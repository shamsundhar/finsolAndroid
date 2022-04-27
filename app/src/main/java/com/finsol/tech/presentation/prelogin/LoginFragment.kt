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
import com.finsol.tech.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class LoginFragment : BaseFragment() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: FragmentLoginBinding
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        loginViewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)


        /* TODO
            Test data login credentails need to be removed - START
         */
            binding.username.setText("C")
            binding.password.setText("mobile12")
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
            loginViewModel.requestLogin(binding.username.text.toString(),binding.password.text.toString())
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

    private fun initObservers() {
        loginViewModel.mState
            .flowWithLifecycle(lifecycle,  Lifecycle.State.STARTED)
            .onEach {
                    it -> processResponse(it)
            }
            .launchIn(lifecycleScope)
    }

    private fun processResponse(state: LoginMarketViewState) {
        when(state){
            is LoginMarketViewState.SuccessResponse -> handleSuccessResponse(state.loginResponseDomainModel)
            is LoginMarketViewState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    private fun handleSuccessResponse(loginResponseDomainModel: LoginResponseDomainModel) {
        if(loginResponseDomainModel.status){
            findNavController().navigate(R.id.to_watchListFragmentFromLogin)
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        if(isLoading){
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
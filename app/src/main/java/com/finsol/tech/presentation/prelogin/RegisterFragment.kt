package com.finsol.tech.presentation.prelogin

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
import com.finsol.tech.R
import com.finsol.tech.data.model.GenericMessageResponse
import com.finsol.tech.databinding.FragmentRegisterBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.util.PreferenceHelper
import com.finsol.tech.util.Utilities
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class RegisterFragment : BaseFragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var progressDialog: ProgressDialog

    private var isObserversInitialized: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        registerViewModel = ViewModelProvider(requireActivity()).get(RegisterViewModel::class.java)
        progressDialog = ProgressDialog(
            context,
            R.style.AppTheme_Dark_Dialog
        )
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(getString(R.string.text_please_wait))
        binding.registerButton.setOnClickListener {
            doRegistration()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers();
    }

    private fun doRegistration() {
        if (validate()) {
            registerViewModel.register(
                binding.userName.toString(),
                binding.userEmail.toString(),
                binding.userPhone.toString()
            )
        }
    }

    private fun validate(): Boolean {
        var result = false
        val _name: String = binding.userName.text.toString().trim()
        val _email: String = binding.userEmail.text.toString().trim()
        val _phone: String = binding.userPhone.text.toString().trim()
        if (_name.length > 0) {
            binding.userName.setError(null)
            if (_email.length > 0) {
                binding.userEmail.setError(null)
                if (_phone.length > 0) {
                    binding.userPhone.setError(null)
                    if (_phone.length < 10) {
                        Utilities.showDialogWithOneButton(
                            context,
                            "Enter valid phone.",
                            null
                        )
                    } else {
                        if (Utilities.isValidEmail(_email)) {
                            result = true
                        } else {
                            Utilities.showDialogWithOneButton(
                                context,
                                "Enter valid email.",
                                null
                            )
                        }

                    }
                } else {
                    binding.userPhone.setError("Field should not be empty")
                }
            } else {
                binding.userEmail.setError("Field should not be empty")
            }
        } else {
            binding.userName.setError("Field should not be empty")
        }
        return result
    }

    private fun initObservers() {
        if (isObserversInitialized) {
            return
        }
        isObserversInitialized = true
        registerViewModel.mState
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { it ->
                processResponse(it)
            }
            .launchIn(lifecycleScope)
    }

    private fun processResponse(state: RegisterViewState) {
        when (state) {
            is RegisterViewState.SuccessResponse -> handleSuccessResponse(state.genericMessageResponse)
            is RegisterViewState.IsLoading -> handleLoading(state.isLoading)
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
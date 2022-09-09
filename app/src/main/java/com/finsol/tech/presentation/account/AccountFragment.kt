package com.finsol.tech.presentation.account

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.finsol.tech.R
import com.finsol.tech.databinding.FragmentAccountBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.rabbitmq.RabbitMQ
import com.finsol.tech.util.AppConstants
import com.finsol.tech.util.AppConstants.*
import com.finsol.tech.util.PreferenceHelper
import com.finsol.tech.util.Utilities
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class AccountFragment: BaseFragment(){
    private lateinit var binding:FragmentAccountBinding
    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var accountViewModel: AccountViewModel
    private var isObserversInitialized : Boolean = false
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    //navigate to watchlist fragment.
                    findNavController().navigate(R.id.watchListFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()
        accountViewModel = ViewModelProvider(requireActivity()).get(AccountViewModel::class.java)
        progressDialog = ProgressDialog(
            context,
            R.style.AppTheme_Dark_Dialog
        )
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(getString(R.string.text_please_wait))

        binding.toolbar.title.visibility = View.VISIBLE
        binding.toolbar.subTitle.visibility = View.VISIBLE
//        binding.toolbar.profilePic.visibility = View.VISIBLE
        binding.toolbar.subTitle.text = preferenceHelper.getString(context, AppConstants.KEY_PREF_NAME, "")

        binding.userEmail.text = preferenceHelper.getString(context, AppConstants.KEY_PREF_EMAIL, "-")
        binding.userName.text = preferenceHelper.getString(context, AppConstants.KEY_PREF_NAME, "-")

        binding.logoutLayout.setOnClickListener {
            Utilities.showDialogWithTwoButton(context, "Do you want to logout from application",
                {
                    //TODO - to phani - can we move below line to handleLogoutSuccessResponse()
                    RabbitMQ.unregisterAll()
                    accountViewModel.doLogout(preferenceHelper.getString(context, AppConstants.KEY_PREF_USER_ID, ""))
                },
                {}, {})

        }
        binding.profileLayout.setOnClickListener {
            findNavController().navigate(R.id.accountProfileFragment)
        }
        binding.settingsLayout.setOnClickListener {
            findNavController().navigate(R.id.accountSettingsFragment)
        }
        binding.helpLayout.setOnClickListener {
            findNavController().navigate(R.id.accountHelpFragment)
        }
        binding.fundsLayout.setOnClickListener {
            findNavController().navigate(R.id.accountFundsFragment)
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers();
    }
    private fun initObservers() {
        if(isObserversInitialized){
            return
        }
        isObserversInitialized = true
        accountViewModel.mState
            .flowWithLifecycle(lifecycle,  Lifecycle.State.STARTED)
            .onEach {
                    it -> processResponse(it)
            }
            .launchIn(lifecycleScope)
    }
    private fun processResponse(state: AccountViewState) {
        when(state){
            is AccountViewState.LogoutSuccessResponse -> handleLogoutSuccessResponse()
            is AccountViewState.IsLoading -> handleLoading(state.isLoading)
            is AccountViewState.ShowToast -> handleToast(state.message)
        }
    }
    private fun handleLogoutSuccessResponse() {

        accountViewModel.resetStateToDefault()
        getValuesBeforeClearingPreference()

        findNavController().navigate(R.id.to_SplashScreenFragmentFromAccount)
    }

    private fun getValuesBeforeClearingPreference() {
        val rememberIPAddress = preferenceHelper.getBoolean(context, KEY_PREF_IP_ADDRESS, false)
        val rememberUserName = preferenceHelper.getBoolean(context, KEY_PREF_USERNAME_REMEMBER, false)
        val ipAddress = preferenceHelper.getString(context, KEY_PREF_IP_ADDRESS_VALUE, "")
        val username = preferenceHelper.getString(context, KEY_PREF_USERNAME_VALUE, "")
        preferenceHelper.clear(context)
        if(rememberIPAddress){
            preferenceHelper.setBoolean(context, KEY_PREF_IP_ADDRESS, true)
            preferenceHelper.setString(context, KEY_PREF_IP_ADDRESS_VALUE, ipAddress)
        }
        if(rememberUserName){
            preferenceHelper.setBoolean(context, KEY_PREF_USERNAME_REMEMBER, true)
            preferenceHelper.setString(context, KEY_PREF_USERNAME_VALUE, username)
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
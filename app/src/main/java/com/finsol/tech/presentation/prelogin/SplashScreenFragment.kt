package com.finsol.tech.presentation.prelogin

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.finsol.tech.FinsolApplication
import com.finsol.tech.R
import com.finsol.tech.data.model.ExchangeEnumModel
import com.finsol.tech.data.model.ExchangeOptionsModel
import com.finsol.tech.databinding.FragmentSplashScreenBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.rabbitmq.RabbitMQ
import com.finsol.tech.util.AppConstants
import com.finsol.tech.util.AppConstants.*
import com.finsol.tech.util.PreferenceHelper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class SplashScreenFragment: BaseFragment() {
    private lateinit var splashScreenViewModel: SplashScreenViewModel
    private lateinit var binding: FragmentSplashScreenBinding
    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var progressDialog: ProgressDialog
    private lateinit var loggedInName:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers();
    }

    private fun initObservers() {
        splashScreenViewModel.mState
            .flowWithLifecycle(lifecycle,  Lifecycle.State.STARTED)
            .onEach {
                    it -> processResponse(it)
            }
            .launchIn(lifecycleScope)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()
        splashScreenViewModel = ViewModelProvider(requireActivity()).get(SplashScreenViewModel::class.java)

        progressDialog = ProgressDialog(
            context,
            R.style.AppTheme_Dark_Dialog
        )
        progressDialog.isIndeterminate = true
        progressDialog.setMessage(getString(R.string.text_getting_details))

        val nightMode:Boolean = preferenceHelper.getBoolean(context, KEY_PREF_DARK_MODE, false)
        loggedInName = preferenceHelper.getString(context, KEY_PREF_NAME, "")
        if(nightMode){
            AppCompatDelegate
                .setDefaultNightMode(
                    AppCompatDelegate
                        .MODE_NIGHT_YES);
        }
        else{
            AppCompatDelegate
                .setDefaultNightMode(
                    AppCompatDelegate
                        .MODE_NIGHT_NO);
        }
        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launchWhenResumed {
                if(preferenceHelper.getString(context, KEY_PREF_IP_ADDRESS_VALUE, "").isNotEmpty()) {
                    splashScreenViewModel.getExchangeEnumData()
                } else {
                    if(loggedInName.equals("")){
                        findNavController().navigate(R.id.to_loginFragment)
                    } else {
                        findNavController().navigate(R.id.to_watchListFragment)
                    }
                }

            }
        }, 500)

        return binding.root
    }

    private fun processResponse(state: SplashScreenViewState) {
        when(state){
            is SplashScreenViewState.IsLoading -> handleLoading(state.isLoading)
            is SplashScreenViewState.ExchangeEnumSuccessResponse -> handleExchangeEnumResponse(state.exchangeEnumData)
            is SplashScreenViewState.ExchangeEnumOptionsSuccessResponse -> handleExchangeOptionsResponse(state.exchangeOptionsData)
        }
    }

    private fun handleExchangeEnumResponse(exchangeEnumData: Array<ExchangeEnumModel>) {
        val map: HashMap<String, String> = HashMap()
        for(model in exchangeEnumData){
            map[model.Key.toString()] = model.Value
        }
        preferenceHelper.saveMap(context, AppConstants.KEY_PREF_EXCHANGE_MAP, map)

        splashScreenViewModel.getExchangeOptionsData()
    }

    private fun handleExchangeOptionsResponse(exchangeOptionsData: Array<ExchangeOptionsModel>) {
        splashScreenViewModel.resetStateToDefault()
        (requireActivity().application as FinsolApplication).setExchangeOptions(exchangeOptionsData)
        if(loggedInName.equals("")){
            findNavController().navigate(R.id.to_loginFragment)
        } else {
            RabbitMQ.subscribeToMarginData(RabbitMQ.preferenceHelper.getString(FinsolApplication.context,KEY_PREF_USER_CTCL,""))
            findNavController().navigate(R.id.to_watchListFragment)
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
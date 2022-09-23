package com.finsol.tech.presentation.prelogin

import android.app.ProgressDialog
import android.net.InetAddresses
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.finsol.tech.FinsolApplication
import com.finsol.tech.R
import com.finsol.tech.data.model.ExchangeEnumModel
import com.finsol.tech.data.model.ExchangeOptionsModel
import com.finsol.tech.databinding.FragmentLoginBinding
import com.finsol.tech.domain.model.LoginResponseDomainModel
import com.finsol.tech.domain.model.ProfileResponseDomainModel
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.rabbitmq.RabbitMQ
import com.finsol.tech.util.AppConstants
import com.finsol.tech.util.AppConstants.*
import com.finsol.tech.util.PreferenceHelper
import com.finsol.tech.util.Utilities
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.regex.Matcher
import java.util.regex.Pattern

@AndroidEntryPoint
class LoginFragment : BaseFragment() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: FragmentLoginBinding
    private lateinit var progressDialog: ProgressDialog
    private var validIPEntered = false
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

        binding.rememberIPAddress.isChecked = preferenceHelper.getBoolean(context, KEY_PREF_IP_ADDRESS, false)
        binding.rememberUsername.isChecked = preferenceHelper.getBoolean(context, KEY_PREF_USERNAME_REMEMBER, false)
        binding.username.setText(preferenceHelper.getString(context, KEY_PREF_USERNAME_VALUE, ""))
        binding.ipAddress.setText(preferenceHelper.getString(context, KEY_PREF_IP_ADDRESS_VALUE, ""))

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

        binding.radioGroupSections.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = group.findViewById<View>(checkedId) as RadioButton
            val isChecked = checkedRadioButton.isChecked
            if (isChecked) {
                if (checkedRadioButton.text.equals("Login")) {
                    loginSectionClicked()
                } else {
                    ipAddressSectionClicked()
                }
            }
        }

        binding.rememberIPAddress.setOnCheckedChangeListener() { compoundButton: CompoundButton, isChecked: Boolean ->
                if (isChecked) {
                    if(validateIPAddress()){
                        validIPEntered = true
                        preferenceHelper.setBoolean(context, KEY_PREF_IP_ADDRESS, true)
                        preferenceHelper.setString(context, KEY_PREF_IP_ADDRESS_VALUE, binding.ipAddress.text.toString())
                    } else {
                        binding.rememberIPAddress.isChecked = false
                    }

                } else {
                    preferenceHelper.setBoolean(context, KEY_PREF_IP_ADDRESS, false)
                }
        }

        binding.rememberUsername.setOnCheckedChangeListener() { compoundButton: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                preferenceHelper.setBoolean(context, KEY_PREF_USERNAME_REMEMBER, true)
            } else {
                preferenceHelper.setBoolean(context, KEY_PREF_USERNAME_REMEMBER, false)
            }
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
        val ipAddress = binding.ipAddress.text.toString()

        if(ipAddress.isNotBlank()){
            //validate ipaddress
            if(InetAddresses.isNumericAddress(ipAddress)){
                validIPEntered = true
                binding.ipAddress.error = null
//                if(binding.rememberIPAddress.isChecked){
//                    preferenceHelper.setBoolean(context, KEY_PREF_IP_ADDRESS, true)
//                    preferenceHelper.setString(context, KEY_PREF_IP_ADDRESS_VALUE, ipAddress)
//                }
//                if(binding.rememberUsername.isChecked){
//                    preferenceHelper.setBoolean(context, KEY_PREF_USERNAME_REMEMBER, true)
//                    preferenceHelper.setString(context, KEY_PREF_USERNAME_VALUE, username)
//                }
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
            } else{
                if(isValidDomain(ipAddress)) {
                    validIPEntered = true
                    binding.ipAddress.error = null
//                    if(binding.rememberIPAddress.isChecked){
//                        preferenceHelper.setString(context, KEY_PREF_IP_ADDRESS_VALUE, ipAddress)
//                    }
//                    if(binding.rememberUsername.isChecked){
//                        preferenceHelper.setString(context, KEY_PREF_USERNAME_VALUE, username)
//                    }
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
                } else {
                    validIPEntered = false
                    ipAddressRadioButtonSelected()
                    binding.ipAddress.error = "Enter valid IP Address"
                    Utilities.showDialogWithOneButton(
                        context,
                        "Enter valid IP Address.",
                        null
                    )
                }

            }
        }else {
            validIPEntered = false
            ipAddressRadioButtonSelected()
            binding.ipAddress.error = "Field should not be empty"
        }

        return result
    }

    private fun validateIPAddress(): Boolean {
        var result = false

        val ipAddress = binding.ipAddress.text.toString()

        if(ipAddress.isNotBlank()){
            //validate ipaddress
            if(InetAddresses.isNumericAddress(ipAddress)){
                binding.ipAddress.error = null
                result = true
            } else{
                if(isValidDomain(ipAddress)){
                    binding.ipAddress.error = null
                    result = true
                } else {
                    ipAddressRadioButtonSelected()
                    binding.ipAddress.error = "Enter valid IP Address"
                    Utilities.showDialogWithOneButton(
                        context,
                        "Enter valid IP Address.",
                        null
                    )
                }
            }
        }else {
            ipAddressRadioButtonSelected()
            binding.ipAddress.error = "Field should not be empty"
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
            is LoginMarketViewState.IsLoading -> handleLoading(state.isLoading)
            is LoginMarketViewState.ShowToast -> Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            is LoginMarketViewState.SuccessResponse -> handleLoginSuccessResponse(state.loginResponseDomainModel)
            is LoginMarketViewState.ProfileSuccessResponse -> handleProfileSuccessResponse(state.profileResponseDomainModel)
            is LoginMarketViewState.ExchangeEnumSuccessResponse -> handleExchangeEnumResponse(state.exchangeEnumData)
            is LoginMarketViewState.ExchangeEnumOptionsSuccessResponse -> handleExchangeOptionsResponse(state.exchangeOptionsData)
            is LoginMarketViewState.displayIPAddressSection -> handleIPAddressSectionDisplay()
            is LoginMarketViewState.displayLoginSection -> handleLoginSectionDisplay()
        }
    }

    private fun handleLoginSectionDisplay() {
        binding.loginSection.visibility = View.VISIBLE
        binding.ipAddressSection.visibility = View.GONE
    }

    private fun handleIPAddressSectionDisplay() {
        binding.loginSection.visibility = View.GONE
        binding.ipAddressSection.visibility = View.VISIBLE
    }

    private fun handleExchangeEnumResponse(exchangeEnumData: Array<ExchangeEnumModel>) {
        val map: HashMap<String, String> = HashMap()
        for(model in exchangeEnumData){
            map[model.Key.toString()] = model.Value
        }
        preferenceHelper.saveMap(context, KEY_PREF_EXCHANGE_MAP, map)
        progressDialog.setMessage(getString(R.string.text_getting_details) + "3/3")
        loginViewModel.getExchangeOptionsData()
    }

    private fun handleExchangeOptionsResponse(exchangeOptionsData: Array<ExchangeOptionsModel>) {
        loginViewModel.resetStateToDefault()
        (requireActivity().application as FinsolApplication).setExchangeOptions(exchangeOptionsData)
        findNavController().navigate(R.id.to_watchListFragmentFromLogin)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun handleLoginSuccessResponse(loginResponseDomainModel: LoginResponseDomainModel) {
        loginViewModel.resetStateToDefault()
        if (loginResponseDomainModel.status) {
            preferenceHelper.setString(
                context,
                KEY_PREF_USER_ID,
                loginResponseDomainModel.userID.toString()
            )
            RabbitMQ.subscribeForUserUpdates(loginResponseDomainModel.userID.toString())
            if(binding.rememberIPAddress.isChecked){
                preferenceHelper.setBoolean(context, KEY_PREF_IP_ADDRESS, true)
                preferenceHelper.setString(context, KEY_PREF_IP_ADDRESS_VALUE, binding.ipAddress.text.toString())
            }
            if(binding.rememberUsername.isChecked){
                preferenceHelper.setBoolean(context, KEY_PREF_USERNAME_REMEMBER, true)
                preferenceHelper.setString(context, KEY_PREF_USERNAME_VALUE, binding.username.text.toString())
            }
            progressDialog.setMessage(getString(R.string.text_getting_details) + "1/3")
            loginViewModel.requestUserProfileDetails(loginResponseDomainModel.userID.toString())
        } else {
            Utilities.showDialogWithOneButton(
                context,
                loginResponseDomainModel.message,
                null
            )
        }
    }

    private fun handleProfileSuccessResponse(profileResponseDomainModel: ProfileResponseDomainModel) {
        loginViewModel.resetStateToDefault()
        preferenceHelper.setString(context, KEY_PREF_NAME, profileResponseDomainModel.name)
        preferenceHelper.setString(context, KEY_PREF_EMAIL, profileResponseDomainModel.emailid)
        preferenceHelper.setString(context, KEY_PREF_PHONE, profileResponseDomainModel.phone)
        progressDialog.setMessage(getString(R.string.text_getting_details) + "2/3")
        loginViewModel.getExchangeEnumData()
    }

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

    fun isValidDomain(str: String?): Boolean {
        // Regex to check valid domain name.
        val regex = ("^((?!-)[A-Za-z0-9-]"
                + "{1,63}(?<!-)\\.)"
                + "+[A-Za-z]{2,6}")

        val p: Pattern = Pattern.compile(regex)

        if (str == null) {
            return false
        }

        val m: Matcher = p.matcher(str)

        return m.matches()
    }

    fun ipAddressRadioButtonSelected() {
        binding.radioButtonIPAddress.isSelected = true
    }

    fun ipAddressSectionClicked() {
        loginViewModel.ipAddressSectionSelected()
    }

    fun loginSectionClicked() {
        loginViewModel.loginSectionSelected()
    }
}
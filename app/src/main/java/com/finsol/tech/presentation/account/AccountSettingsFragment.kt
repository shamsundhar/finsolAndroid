package com.finsol.tech.presentation.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import com.finsol.tech.R
import com.finsol.tech.databinding.FragmentAccountSettingsBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.utilities.AppConstants.KEY_PREF_DARK_MODE
import com.finsol.tech.utilities.PreferenceHelper

class AccountSettingsFragment: BaseFragment() {
    private lateinit var binding: FragmentAccountSettingsBinding
    private lateinit var preferenceHelper: PreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountSettingsBinding.inflate(inflater, container, false)

//        usernameET.setText("aadhim@gmail.com");
//        passwordET.setText("aadhim!");
        preferenceHelper = PreferenceHelper.getPrefernceHelperInstance()
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.title2.visibility = View.VISIBLE
        val curNightMode = preferenceHelper.getBoolean(context, KEY_PREF_DARK_MODE, false)
        binding.darkModeSwitch.isChecked = curNightMode
        binding.toolbar.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.changePasswordLayout.setOnClickListener {
            findNavController().navigate(R.id.accountChangePasswordFragment)
        }
        binding.darkModeSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
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
            preferenceHelper.setBoolean(context, KEY_PREF_DARK_MODE, b)
        }
        return binding.root
    }
}
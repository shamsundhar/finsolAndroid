package com.finsol.tech.presentation.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.finsol.tech.R
import com.finsol.tech.databinding.FragmentAccountSettingsBinding
import com.finsol.tech.presentation.base.BaseFragment

class AccountSettingsFragment: BaseFragment() {
    private lateinit var binding: FragmentAccountSettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountSettingsBinding.inflate(inflater, container, false)
        binding.toolbar.backButton.visibility = View.VISIBLE
//        binding.toolbar.subTitle.visibility = View.GONE
//        binding.toolbar.title.visibility = View.GONE
        binding.toolbar.title2.visibility = View.VISIBLE
        binding.toolbar.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.changePasswordLayout.setOnClickListener {
            findNavController().navigate(R.id.accountChangePasswordFragment)
        }
        return binding.root
    }
}
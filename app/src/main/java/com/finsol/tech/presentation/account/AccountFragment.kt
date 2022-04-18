package com.finsol.tech.presentation.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.finsol.tech.R
import com.finsol.tech.databinding.FragmentAccountBinding
import com.finsol.tech.presentation.base.BaseFragment


class AccountFragment: BaseFragment(){
    private lateinit var binding:FragmentAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        binding.toolbar.title.visibility = View.VISIBLE
        binding.toolbar.subTitle.visibility = View.VISIBLE
        binding.toolbar.profilePic.visibility = View.VISIBLE

        binding.logoutLayout.setOnClickListener {
            findNavController().navigate(R.id.to_loginFragmentFromAccount)
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
}
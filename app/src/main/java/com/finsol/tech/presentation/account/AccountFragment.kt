package com.finsol.tech.presentation.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.finsol.tech.R
import com.finsol.tech.databinding.FragmentAccountBinding


class AccountFragment: Fragment(){
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
        binding.logoutLayout.setOnClickListener {
            findNavController().navigate(R.id.to_loginFragmentFromAccount)
        }
        return binding.root
    }
}
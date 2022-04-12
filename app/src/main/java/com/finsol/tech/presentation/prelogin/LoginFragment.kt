package com.finsol.tech.presentation.prelogin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.finsol.tech.R
import com.finsol.tech.databinding.FragmentLoginBinding

class LoginFragment: Fragment() {
    private lateinit var binding: FragmentLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentLoginBinding.inflate(inflater, container, false)

       binding.loginBtn.setOnClickListener {
           findNavController().navigate(R.id.to_watchListFragmentFromLogin)
       }
        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(R.id.to_forgotPasswordFragmentFromLogin)
        }
        binding.signUp.setOnClickListener {
            findNavController().navigate(R.id.to_registerFragmentFromLogin)
        }

       return binding.root
    }
}
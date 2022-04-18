package com.finsol.tech.presentation.prelogin

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.finsol.tech.R
import com.finsol.tech.databinding.FragmentLoginBinding
import com.finsol.tech.presentation.base.BaseFragment

class LoginFragment: BaseFragment() {
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
        binding.showPassBtn.setOnClickListener {
           showHidePass(it)
        }

       return binding.root
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
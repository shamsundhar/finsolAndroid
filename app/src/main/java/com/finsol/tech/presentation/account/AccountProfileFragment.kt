package com.finsol.tech.presentation.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.finsol.tech.R
import com.finsol.tech.databinding.FragmentAccountProfileBinding

class AccountProfileFragment: Fragment() {
    private lateinit var binding: FragmentAccountProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountProfileBinding.inflate(inflater, container, false)
        binding.updateButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.title2.visibility = View.VISIBLE
        binding.toolbar.title.visibility = View.GONE
        binding.toolbar.subTitle.visibility = View.GONE
        return binding.root
    }
}
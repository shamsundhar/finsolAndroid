package com.finsol.tech.presentation.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.finsol.tech.databinding.FragmentOrderHistoryDetailsBinding

class OrderHistoryDetailsFragment: Fragment() {
    private lateinit var binding: FragmentOrderHistoryDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderHistoryDetailsBinding.inflate(inflater, container, false)
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.title.visibility = View.GONE
        binding.toolbar.subTitle.visibility = View.GONE
        binding.toolbar.title2.visibility = View.VISIBLE

        binding.toolbar.backButton.setOnClickListener { activity?.onBackPressed() }

        return binding.root
    }
}
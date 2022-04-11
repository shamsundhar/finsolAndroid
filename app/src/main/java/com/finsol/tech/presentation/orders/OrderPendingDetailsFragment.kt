package com.finsol.tech.presentation.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.finsol.tech.R
import com.finsol.tech.databinding.FragmentPendingOrderDetailsBinding

class OrderPendingDetailsFragment: Fragment() {
    private lateinit var binding: FragmentPendingOrderDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPendingOrderDetailsBinding.inflate(inflater, container, false)
        binding.cancelButton.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.modifyButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("selectedMode", "Buy")
            findNavController().navigate(R.id.buySellFragment, bundle)
        }
        return binding.root
    }
}
package com.finsol.tech.presentation.buysell

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.finsol.tech.R
import com.finsol.tech.databinding.FragmentBuySellBinding


class BuySellFragment: Fragment() {
    private lateinit var binding: FragmentBuySellBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBuySellBinding.inflate(inflater, container, false)
        val mode: String? = arguments?.getString("selectedMode")
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.subTitle.visibility = View.GONE
        binding.toolbar.title.visibility = View.GONE
        binding.toolbar.title2.visibility = View.VISIBLE
        binding.priceET.isEnabled = false

        if(mode.equals("Buy")){
            binding.radioButtonBuy.isChecked = true
            binding.rootLayout.setBackgroundColor(resources.getColor(R.color.alice_blue))
        } else{
            binding.radioButtonSell.isChecked = true
            binding.rootLayout.setBackgroundColor(resources.getColor(R.color.lavender_blush))
        }

        binding.radioGroupBuySell.setOnCheckedChangeListener { group, checkedId ->
           val checkedRadioButton = group.findViewById<View>(checkedId) as RadioButton
           val isChecked = checkedRadioButton.isChecked
           if (isChecked) {
                if(checkedRadioButton.text.equals("Buy")){
                    binding.rootLayout.setBackgroundColor(resources.getColor(R.color.alice_blue))
                } else {
                    binding.rootLayout.setBackgroundColor(resources.getColor(R.color.lavender_blush))
                }
            }
        }

        binding.confirmButton.setOnClickListener {
            findNavController().navigate(R.id.ordersFragment)
        }

        return binding.root
    }

}
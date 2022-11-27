package com.finsol.tech.presentation.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.finsol.tech.R
import com.finsol.tech.databinding.FragmentNotificationsBinding
import com.finsol.tech.presentation.portfolio.PortfolioViewModel

class NotificationsFragment : Fragment() {


    private lateinit var binding: FragmentNotificationsBinding
    private lateinit var viewModel: NotificationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(NotificationsViewModel::class.java)

        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.title2.visibility = View.VISIBLE
        binding.toolbar.title2.text = getString(R.string.text_notifications)
        binding.toolbar.clearNotifications.visibility = View.VISIBLE

        binding.notiRC.layoutManager = LinearLayoutManager(context)
        binding.notiRC.adapter = NotificationsAdapter(requireContext(),resources,viewLifecycleOwner)

        binding.toolbar.backButton.setOnClickListener{
            requireActivity().onBackPressed()
        }

        binding.toolbar.clearNotifications.setColorFilter(ContextCompat.getColor(context!!, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);

        binding.toolbar.clearNotifications.setOnClickListener {
            viewModel.clearAllNotifications()
        }

        return binding.root
    }


}
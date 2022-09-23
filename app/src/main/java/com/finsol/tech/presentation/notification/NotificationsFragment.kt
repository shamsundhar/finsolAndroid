package com.finsol.tech.presentation.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.finsol.tech.R
import com.finsol.tech.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {


    private lateinit var binding: FragmentNotificationsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.title2.visibility = View.VISIBLE
        binding.toolbar.title2.text = getString(R.string.text_notifications)

        binding.notiRC.layoutManager = LinearLayoutManager(context)
        binding.notiRC.adapter = NotificationsAdapter(requireContext(),resources)

        binding.toolbar.backButton.setOnClickListener{
            requireActivity().onBackPressed()
        }

        return binding.root
    }


}
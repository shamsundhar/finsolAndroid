package com.finsol.tech.presentation.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.finsol.tech.FinsolApplication
import com.finsol.tech.R
import com.finsol.tech.data.model.Contracts
import com.finsol.tech.data.model.GetAllContractsResponse
import com.finsol.tech.databinding.FragmentWatchlistSearchBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.watchlist.adapter.WatchListSearchAdapter

class WatchListSearchFragment : BaseFragment() {
    private lateinit var binding: FragmentWatchlistSearchBinding
    private lateinit var allContractsResponse: GetAllContractsResponse
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWatchlistSearchBinding.inflate(inflater, container, false)
        allContractsResponse =
            (requireActivity().application as FinsolApplication).getAllContracts()
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.searchET.visibility = View.VISIBLE

        binding.toolbar.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        // this creates a vertical layout Manager
        binding.watchListRecyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = WatchListSearchAdapter()

        val watchList1Ids = allContractsResponse.watchlist2.map { it.securityID }
        allContractsResponse.allContracts.map {
            if (it.securityID in watchList1Ids) {
                it.isAddedToWatchList = true
            }
        }
        adapter.updateList(allContractsResponse.allContracts)
        adapter.setOnItemClickListener(object : WatchListSearchAdapter.ClickListener {
            override fun onItemClick(model: Contracts) {
                val bundle = Bundle()
//                bundle.putParcelable("selectedModel", model)
                findNavController().navigate(R.id.bottomSheetDialog, bundle)
            }
        })

        // Setting the Adapter with the recyclerview
        binding.watchListRecyclerView.adapter = adapter


        return binding.root
    }
}
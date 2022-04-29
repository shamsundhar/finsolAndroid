package com.finsol.tech.presentation.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.finsol.tech.R
import com.finsol.tech.data.model.Contracts
import com.finsol.tech.databinding.FragmentWatchlistChild1Binding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.watchlist.adapter.ChildWatchListAdapter3.ClickListener
import com.finsol.tech.presentation.watchlist.adapter.ChildWatchListAdapter3


class ChildWatchListFragment3: BaseFragment() {
    private lateinit var binding: FragmentWatchlistChild1Binding
    private lateinit var adapter3: ChildWatchListAdapter3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWatchlistChild1Binding.inflate(inflater, container, false)
        binding.searchET.setOnClickListener{
            findNavController().navigate(R.id.watchListSearchFragment)
        }

        binding.watchListRecyclerView.layoutManager = LinearLayoutManager(context)


        adapter3 = ChildWatchListAdapter3()
        adapter3.setOnItemClickListener(object:ClickListener {
            override fun onItemClick(model: Contracts) {
                val bundle = Bundle()
//                bundle.putParcelable("selectedModel", model)
                findNavController().navigate(R.id.to_watchListPartialDetailsFragment, bundle)
            }
        })

        // Setting the Adapter with the recyclerview
        binding.watchListRecyclerView.adapter = adapter3
        return binding.root;
    }
    fun updateWatchListData(list:List<Contracts>) {

        adapter3.updateList(list)
    }
}
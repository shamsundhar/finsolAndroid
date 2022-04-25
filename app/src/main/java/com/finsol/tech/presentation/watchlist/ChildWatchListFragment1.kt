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
import com.finsol.tech.databinding.FragmentWatchlistChild1Binding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.watchlist.adapter.ChildWatchListAdapter1
import com.finsol.tech.presentation.watchlist.adapter.ChildWatchListAdapter1.ClickListener


class ChildWatchListFragment1: BaseFragment() {
    private lateinit var binding: FragmentWatchlistChild1Binding
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

        // this creates a vertical layout Manager
        binding.watchListRecyclerView.layoutManager = LinearLayoutManager(context)

        // ArrayList of class ItemsViewModel
        val data = ArrayList<WatchListModel>()

        // This loop will create 20 Views containing
        // the image with the count of view
        for (i in 1..20) {
            data.add(WatchListModel("Name"+i, "price " + i, "time"+i, "city"+i, "value"+i))
        }

        // This will pass the ArrayList to our Adapter
        val adapter = ChildWatchListAdapter1(data)
        adapter.setOnItemClickListener(object:ClickListener {
            override fun onItemClick(model: WatchListModel) {
                val bundle = Bundle()
                bundle.putParcelable("selectedModel", model)
                findNavController().navigate(R.id.to_watchListPartialDetailsFragment, bundle)
            }
        })

        // Setting the Adapter with the recyclerview
        binding.watchListRecyclerView.adapter = adapter
        return binding.root;
    }
}
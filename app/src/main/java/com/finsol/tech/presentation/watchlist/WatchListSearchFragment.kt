package com.finsol.tech.presentation.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.finsol.tech.R
import com.finsol.tech.databinding.FragmentWatchlistSearchBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.watchlist.adapter.ChildWatchListAdapter1
import com.finsol.tech.presentation.watchlist.adapter.WatchListSearchAdapter

class WatchListSearchFragment: BaseFragment() {
    private lateinit var binding : FragmentWatchlistSearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWatchlistSearchBinding.inflate(inflater, container, false)
//        binding.toolbar.profilePic.visibility = View.GONE
//        binding.toolbar.title.visibility = View.GONE
//        binding.toolbar.subTitle.visibility = View.GONE
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.searchET.visibility = View.VISIBLE

        binding.toolbar.backButton.setOnClickListener {
            activity?.onBackPressed()
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
        val adapter = WatchListSearchAdapter(data)
        adapter.setOnItemClickListener(object: WatchListSearchAdapter.ClickListener {
            override fun onItemClick(model: WatchListModel) {
                val bundle = Bundle()
                bundle.putParcelable("selectedModel", model)
                findNavController().navigate(R.id.bottomSheetDialog, bundle)
            }
        })

        // Setting the Adapter with the recyclerview
        binding.watchListRecyclerView.adapter = adapter


        return binding.root
    }
}
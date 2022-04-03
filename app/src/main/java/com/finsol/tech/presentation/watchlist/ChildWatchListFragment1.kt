package com.finsol.tech.presentation.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.finsol.tech.R
import com.finsol.tech.presentation.watchlist.adapter.ChildWatchListAdapter1
import com.finsol.tech.presentation.watchlist.adapter.ChildWatchListAdapter1.ClickListener


class ChildWatchListFragment1: Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater.inflate(R.layout.fragment_watchlist_child1, container, false)
        val recyclerview = view.findViewById<RecyclerView>(R.id.watchListRecyclerView)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(context)

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
                val bottomSheet = BottomSheetDialog(model)
                fragmentManager?.let {
                    bottomSheet.show(
                        it,
                        "ModalBottomSheet"
                    )
                }
            }
        })

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter
        return view;
    }
}
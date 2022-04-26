package com.finsol.tech.presentation.portfolio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.finsol.tech.R
import com.finsol.tech.databinding.FragmentPortfolioBinding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.orders.adapter.OrdersPendingAdapter
import com.finsol.tech.presentation.portfolio.adapter.PortfolioAdapter
import com.finsol.tech.presentation.watchlist.WatchListModel

class PortfolioFragment: BaseFragment(){
    private lateinit var binding: FragmentPortfolioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPortfolioBinding.inflate(inflater, container, false)
        binding.toolbar.title.visibility = View.VISIBLE
        binding.toolbar.subTitle.visibility = View.VISIBLE
        binding.toolbar.profilePic.visibility = View.VISIBLE
        // this creates a vertical layout Manager
        binding.portfolioRecyclerView.layoutManager = LinearLayoutManager(context)

        // ArrayList of class ItemsViewModel
        val data = ArrayList<WatchListModel>()

        // This loop will create 20 Views containing
        // the image with the count of view
        for (i in 1..20) {
            data.add(WatchListModel("Name"+i, "price " + i, "time"+i, "city"+i, "value"+i))
        }
        // This will pass the ArrayList to our Adapter
        val adapter = PortfolioAdapter(data)
        adapter.setOnItemClickListener(object: PortfolioAdapter.ClickListener {
            override fun onItemClick(model: WatchListModel) {
                val bundle = Bundle()
                bundle.putParcelable("selectedModel", model)
                findNavController().navigate(R.id.to_portfolioPartialDetailsFragment, bundle)
            }
        })

        // Setting the Adapter with the recyclerview
        binding.portfolioRecyclerView.adapter = adapter


        return binding.root
    }
}
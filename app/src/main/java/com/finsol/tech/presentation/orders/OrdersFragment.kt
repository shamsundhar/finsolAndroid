package com.finsol.tech.presentation.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.finsol.tech.R
import com.finsol.tech.databinding.FragmentOrdersBinding
import com.finsol.tech.presentation.orders.adapter.OrdersPendingAdapter
import com.finsol.tech.presentation.watchlist.WatchListModel
import com.finsol.tech.presentation.watchlist.adapter.ChildWatchListAdapter1

class OrdersFragment: Fragment(){
    private lateinit var binding: FragmentOrdersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        binding.noOrdersSection.visibility = View.VISIBLE

        binding.radioGroupOrders.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = group.findViewById<View>(checkedId) as RadioButton
            val isChecked = checkedRadioButton.isChecked
            if (isChecked) {
                if(checkedRadioButton.text.equals("Pending Orders")){
                    binding.noOrdersSection.visibility = View.GONE
                    binding.pendingOrdersSection.visibility = View.VISIBLE
                    binding.ordersHistorySection.visibility = View.GONE
                } else {
                    binding.noOrdersSection.visibility = View.GONE
                    binding.pendingOrdersSection.visibility = View.GONE
                    binding.ordersHistorySection.visibility = View.VISIBLE
                }
            }
        }

        // this creates a vertical layout Manager
        binding.pendingRecyclerView.layoutManager = LinearLayoutManager(context)

        // ArrayList of class ItemsViewModel
        val data = ArrayList<WatchListModel>()

        // This loop will create 20 Views containing
        // the image with the count of view
        for (i in 1..20) {
            data.add(WatchListModel("Name"+i, "price " + i, "time"+i, "city"+i, "value"+i))
        }

        // This will pass the ArrayList to our Adapter
        val adapter = OrdersPendingAdapter(data)
        adapter.setOnItemClickListener(object: OrdersPendingAdapter.ClickListener {
            override fun onItemClick(model: WatchListModel) {
                val bundle = Bundle()
                bundle.putParcelable("selectedModel", model)
                findNavController().navigate(R.id.bottomSheetDialog, bundle)
            }
        })

        // Setting the Adapter with the recyclerview
        binding.pendingRecyclerView.adapter = adapter


        return binding.root
    }
}
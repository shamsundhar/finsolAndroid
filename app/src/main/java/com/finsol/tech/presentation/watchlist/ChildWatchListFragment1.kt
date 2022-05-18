package com.finsol.tech.presentation.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.finsol.tech.R
import com.finsol.tech.data.model.Contracts
import com.finsol.tech.data.model.Market
import com.finsol.tech.databinding.FragmentWatchlistChild1Binding
import com.finsol.tech.presentation.base.BaseFragment
import com.finsol.tech.presentation.watchlist.adapter.ChildWatchListAdapter1
import com.finsol.tech.presentation.watchlist.adapter.ChildWatchListAdapter1.ClickListener
import com.finsol.tech.rabbitmq.MySingletonViewModel
import com.finsol.tech.rabbitmq.RabbitMQ
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChildWatchListFragment1: BaseFragment() {
    private lateinit var mySingletonViewModel: MySingletonViewModel
    private lateinit var binding: FragmentWatchlistChild1Binding
    private lateinit var adapter1: ChildWatchListAdapter1
    private var isViewCreated = false;
    private var list:List<Contracts>? = null

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
             val bundle = bundleOf("watchListNumber" to 1)
             findNavController().navigate(R.id.to_watchListSearchFragment, bundle)
         }

        mySingletonViewModel  = MySingletonViewModel.getMyViewModel(this)

        mySingletonViewModel.getContracts()?.observe(viewLifecycleOwner) {
            updateListWithNewMarketData(it)
        }

        binding.watchListRecyclerView.layoutManager = LinearLayoutManager(context)

        adapter1 = ChildWatchListAdapter1()
        adapter1.setOnItemClickListener(object:ClickListener {
            override fun onItemClick(model: Contracts) {
                val bundle = Bundle()
                bundle.putParcelable("selectedModel", model)
                findNavController().navigate(R.id.to_watchListPartialDetailsFragment, bundle)
            }

            override fun onItemLongClick():Boolean {
                val bundle = bundleOf("watchListNumber" to 1)
                findNavController().navigate(R.id.to_watchListEditFragment, bundle)
                return true
            }
        })

        // Setting the Adapter with the recyclerview
        binding.watchListRecyclerView.adapter = adapter1
        list?.let {
            updateWatchListData(it)
        }
        isViewCreated = true
        return binding.root;
    }

    private fun updateListWithNewMarketData(hashMap: HashMap<String, Market>) {
        this.list?.forEach{ contract ->
            println("Here is my security id "+contract.securityID)
            println("Here is my hashmap data "+hashMap[contract.securityID])
            val securityID = contract.securityID
            val markertData = hashMap[contract.securityID]
            if(securityID.equals(markertData?.securityID,true)){
                contract.closePrice = markertData?.ClosePrice?.toInt() ?: 0
            }
        }
    }

    fun updateWatchListData(list:List<Contracts>) {
        this.list = list
        subscribeForMarketDataBySecurityID()
        if(isViewCreated){
            adapter1.updateList(list)
            if (list.size == 0) {
                binding.watchListRecyclerView.visibility = View.GONE
                binding.noItems.visibility = View.VISIBLE
            } else {
                binding.watchListRecyclerView.visibility = View.VISIBLE
                binding.noItems.visibility = View.GONE
            }
        }
    }

    private fun subscribeForMarketDataBySecurityID() {
        this.list?.forEach{ contract ->
            lifecycleScope.launch(Dispatchers.IO) {
                RabbitMQ.subscribeForMarketData(contract.securityID.toString())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        list?.let {
            updateWatchListData(it)
        }
    }
}
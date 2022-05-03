package com.finsol.tech.presentation.orders.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.finsol.tech.R
import com.finsol.tech.data.model.Contracts
import com.finsol.tech.data.model.PendingOrderModel
import com.finsol.tech.presentation.watchlist.WatchListModel

class OrdersPendingAdapter: RecyclerView.Adapter<OrdersPendingAdapter.ViewHolder>(){
    lateinit var clickListener:ClickListener
    private lateinit var mList: List<PendingOrderModel>
    fun updateList(list: List<PendingOrderModel>) {
        mList = list
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pending_orders, parent, false)

        return ViewHolder(view)
    }

    fun setOnItemClickListener(listener:ClickListener){
        clickListener = listener
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        // sets the text to the textview from our itemHolder class
        holder.symbolName.text = itemsViewModel.Symbol_Name
        holder.symbolPrice.text = itemsViewModel.StopPrice.toString()
        holder.symbolValue.text = itemsViewModel.SecurityID
        holder.root.setOnClickListener {
            clickListener.onItemClick(itemsViewModel)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        if(::mList.isInitialized){
            return mList.size
        }
        else{
            return 0
        }
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        //TODO use binding below
        val symbolName: TextView = itemView.findViewById(R.id.symbolName)
        val symbolPrice: TextView = itemView.findViewById(R.id.symbolPrice)
        val symbolValue: TextView = itemView.findViewById(R.id.symbolValue)
        val root: View = itemView.findViewById(R.id.root)
    }

    interface ClickListener {
        fun onItemClick(model: PendingOrderModel)
    }
}
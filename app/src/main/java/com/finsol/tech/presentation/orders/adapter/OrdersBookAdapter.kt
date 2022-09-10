package com.finsol.tech.presentation.orders.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.finsol.tech.R
import com.finsol.tech.data.model.OrderHistoryModel
import com.finsol.tech.data.model.RejectedCancelledOrdersResponse
import com.finsol.tech.util.Utilities
import java.util.*

class OrdersBookAdapter(private val context: Context?, private val resources: Resources): RecyclerView.Adapter<OrdersBookAdapter.ViewHolder>(){
    lateinit var clickListener:ClickListener
    private lateinit var mList: List<RejectedCancelledOrdersResponse>
    fun updateList(list: List<RejectedCancelledOrdersResponse>) {
        mList = list
        notifyDataSetChanged()
    }
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_orders_history, parent, false)

        return ViewHolder(view)
    }

    fun setOnItemClickListener(listener:ClickListener){
        clickListener = listener
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        // sets the text to the textview from our itemHolder class
        holder.symbolName.text = "TBD"
        itemsViewModel.ContractYear.let {
            holder.symbolExpiry.text =  "TBD"
        }
        holder.symbolPrice.text = itemsViewModel.Price.toString()


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        //TODO use binding below
        val symbolName: TextView = itemView.findViewById(R.id.symbolName)
        val symbolExpiry: TextView = itemView.findViewById(R.id.symbolExpiry)
        val symbolPrice: TextView = itemView.findViewById(R.id.symbolPrice)
        val status1: TextView = itemView.findViewById(R.id.status1)
        val symbolLtp: TextView = itemView.findViewById(R.id.symbolLtp)
        val symbolPnl:TextView = itemView.findViewById(R.id.symbolPnl)
        val orderQuantity: TextView = itemView.findViewById(R.id.orderQuantity)
        val root: View = itemView.findViewById(R.id.root)
    }

    interface ClickListener {
        fun onItemClick(model: OrderHistoryModel)
    }
}
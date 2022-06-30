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
import com.finsol.tech.util.Utilities
import java.util.*

class OrdersHistoryAdapter(private val context: Context?, private val resources: Resources): RecyclerView.Adapter<OrdersHistoryAdapter.ViewHolder>(){
    lateinit var clickListener:ClickListener
    private lateinit var mList: List<OrderHistoryModel>
    fun updateList(list: List<OrderHistoryModel>) {
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
        holder.symbolName.text = itemsViewModel.Symbol_Name
        itemsViewModel.ContractYear.let {
            holder.symbolExpiry.text =  itemsViewModel.maturityDay.toString()+" "+ Utilities.getMonthName(
                itemsViewModel.ContractYear.substring(4).toInt(),
                Locale.US, true)
        }
        holder.symbolPrice.text = itemsViewModel.Price.toString()
        val ltp = 1
        val pnl:Double = itemsViewModel.Order_Type.let {
            when (it) {
                1 -> (itemsViewModel.Price - ltp).toDouble()
                2 -> (ltp - itemsViewModel.Price).toDouble()
                else -> 0.0
            }
        }
        if(pnl >= 0.0){
            context?.let {holder.symbolPnl.setTextColor(ContextCompat.getColor(it,(R.color.green)))}
        } else {
            context?.let {holder.symbolPnl.setTextColor(ContextCompat.getColor(it,(R.color.red)))}
        }
        holder.symbolPnl.text = java.lang.String.format(resources.getString(R.string.text_pnl_percentage), pnl.toString()+"%")
        holder.symbolLtp.text = if(itemsViewModel?.LTP.isNullOrBlank()){"-"}else{java.lang.String.format(resources.getString(R.string.text_ltp), itemsViewModel.LTP)}
        holder.orderQuantity.text = java.lang.String.format(resources.getString(R.string.text_work_quantity), itemsViewModel.OrderQty)
        holder.status1.text = itemsViewModel.Order_Type.let {
            when(it){
                1 -> "Buy"
                2 -> "Sell"
                else -> ""
            }
        }

//        itemsViewModel.Order_Type.let {
//            when(it){
//                1 -> holder.status1.setTextColor(ContextCompat.getColor(context, (R.color.green)))
//                2 -> holder.status1.setTextColor(ContextCompat.getColor(context, (R.color.red)))
//                else -> ""
//            }
//        }
        context?.let {
            itemsViewModel.Order_Type.let {
                when(it){
                    1 -> {
//                        holder.status1.setTextColor(ContextCompat.getColor(context, (R.color.green)))
                        holder.status1.setBackgroundResource(R.drawable.bg_aliceblue_round_corners)
                    }
                    2 -> {
//                        holder.status1.setTextColor(ContextCompat.getColor(context, (R.color.red)))
                        holder.status1.setBackgroundResource(R.drawable.bg_light_red_round_corner)
                    }
                    else -> ""
                }
            }
        }
        val padding20dp = 20
        val padding8dp = 8
        val density = resources.displayMetrics.density
        val paddingSidePixel = (padding20dp * density)
        val paddingTopPixel = (padding8dp * density)
        holder.status1.setPadding(paddingSidePixel.toInt(), paddingTopPixel.toInt(),paddingSidePixel.toInt(),paddingTopPixel.toInt())
        holder.root.setOnClickListener {
            clickListener.onItemClick(itemsViewModel)
        }

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
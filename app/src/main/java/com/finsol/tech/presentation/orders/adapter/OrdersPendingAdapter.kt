package com.finsol.tech.presentation.orders.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.finsol.tech.R
import com.finsol.tech.data.model.Contracts
import com.finsol.tech.data.model.PendingOrderModel
import com.finsol.tech.util.Utilities
import java.util.*
import kotlin.collections.HashMap

class OrdersPendingAdapter(private val resources: Resources) : RecyclerView.Adapter<OrdersPendingAdapter.ViewHolder>(){
    lateinit var clickListener:ClickListener
    private lateinit var mList: List<PendingOrderModel>
    private var exchangeMap:HashMap<String, String> = HashMap()
    fun updateList(list: List<PendingOrderModel>) {
        mList = list
        notifyDataSetChanged()
    }
    fun exchangeMap(exchangeMap:HashMap<String, String>) {
        this.exchangeMap = exchangeMap
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
        itemsViewModel.ContractYear.let {
            holder.symbolExpiry.text = itemsViewModel.MaturityDay.toString() +" "+ Utilities.getMonthName(
                itemsViewModel.ContractYear.substring(4).toInt(),
                Locale.US, true)
        }
        holder.symbolPrice.text = java.lang.String.format(resources.getString(R.string.text_avg_amt), itemsViewModel.PriceSend)

        holder.symbolLtp.text = if(itemsViewModel?.LTP.isNullOrBlank()){"-"}else{java.lang.String.format(resources.getString(R.string.text_cumulative_pnl), itemsViewModel?.LTP.toDouble())}
        holder.workQuantity.text = java.lang.String.format(resources.getString(R.string.text_work_quantity), itemsViewModel.WorkQty)
        holder.filledOrderedQuantity.text = java.lang.String.format(resources.getString(R.string.text_filled_ordered_quantity), itemsViewModel.FilledQty, itemsViewModel.OrderQty)
        holder.status2.text = itemsViewModel.Order_Type.let {
            when(it){
                 1 -> "Buy"
                 2 -> "Sell"
                else -> ""
            }
        }
        holder.status1.text = "Pending"
        holder.symbolMarketType.text = itemsViewModel.Market_Type.let {
            when(it){
                1 -> "MARKET"
                2 -> "LIMIT"
                3 -> "STOP"
                4 -> "STOPLIMIT"
                else -> ""
            }
        }
        holder.symbolExchange.text = exchangeMap.get(itemsViewModel.Exchange_Name.toString())
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
        val symbolExpiry: TextView = itemView.findViewById(R.id.symbolExpiry)
        val symbolPrice: TextView = itemView.findViewById(R.id.symbolPrice)
        val symbolLtp: TextView = itemView.findViewById(R.id.symbolLtp)
        val status2: TextView = itemView.findViewById(R.id.status2)
        val status1: TextView = itemView.findViewById(R.id.status1)
        val symbolMarketType:TextView = itemView.findViewById(R.id.symbolMarketType)
        val symbolExchange:TextView = itemView.findViewById(R.id.symbolExchange)
        val workQuantity: TextView = itemView.findViewById(R.id.workQuantity)
        val filledOrderedQuantity: TextView = itemView.findViewById(R.id.filledOrderedQuantity)
        val root: View = itemView.findViewById(R.id.root)
    }

    interface ClickListener {
        fun onItemClick(model: PendingOrderModel)
    }
}
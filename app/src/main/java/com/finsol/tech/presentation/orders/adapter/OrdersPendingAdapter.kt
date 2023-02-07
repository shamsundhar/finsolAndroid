package com.finsol.tech.presentation.orders.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.finsol.tech.FinsolApplication
import com.finsol.tech.R
import com.finsol.tech.data.model.PendingOrderModel
import com.finsol.tech.rabbitmq.RabbitMQ
import com.finsol.tech.util.Utilities
import java.util.*
import kotlin.collections.HashMap

class OrdersPendingAdapter(private val context: Context,private val resources: Resources) : RecyclerView.Adapter<OrdersPendingAdapter.ViewHolder>(){
    lateinit var clickListener:ClickListener
    private lateinit var mList: List<PendingOrderModel>
    private var exchangeMap:HashMap<String, String> = HashMap()
    fun updateList(list: List<PendingOrderModel>) {
        mList = list
        mList = mList.sortedByDescending { it.ExchangeTransactTime }
        mList.forEach {
            RabbitMQ.subscribeForMarketData(it.SecurityID)
        }
        notifyDataSetChanged()
    }
    fun exchangeMap(exchangeMap:HashMap<String, String>) {
        this.exchangeMap = exchangeMap
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pending_orders_new, parent, false)

        return ViewHolder(view)
    }

    fun setOnItemClickListener(listener:ClickListener){
        clickListener = listener
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        // sets the text to the textview from our itemHolder class

        holder.date.text = Utilities.convertOrderHistoryTimeWithDate(itemsViewModel.ExchangeTransactTime)
        holder.statusQuantity.text = "Pending | Qty: ${itemsViewModel.FilledQty}/${itemsViewModel.OrderQty}"

        val contract = (context.applicationContext as FinsolApplication).getContractBySecurityID(itemsViewModel.SecurityID)
        holder.symbolName.text = contract?.symbolName
        contract?.expiry.let {
            holder.symbolExpiry.text = contract?.maturityDay +"-"+ Utilities.getMonthName(
                contract?.expiry?.substring(4,6)!!.toInt(),
                Locale.US, true) + "-" + contract.expiry.substring(0, 4)
        }

        holder.symbolPrice.text = java.lang.String.format(resources.getString(R.string.text_amt), itemsViewModel.PriceSend)

//        holder.workQuantity.text = java.lang.String.format(resources.getString(R.string.text_work_quantity), itemsViewModel.WorkQty)
//        holder.filledOrderedQuantity.text = java.lang.String.format(resources.getString(R.string.text_filled_ordered_quantity), itemsViewModel.FilledQty, itemsViewModel.OrderQty)
//        holder.status2.text = itemsViewModel.Order_Type.let {
//            when(it){
//                 1 -> "Buy"
//                 2 -> "Sell"
//                else -> ""
//            }
//        }

        setTextColorBasedOnOrderType(itemsViewModel.Order_Type,holder)

        val marketType = itemsViewModel.Market_Type.let {
            when(it){
                1 -> "MARKET"
                2 -> "LIMIT"
                3 -> "STOP"
                4 -> "STOPLIMIT"
                5 -> "ICEBERG"
                else -> ""
            }
        }

        holder.symbolLtp.text = "$marketType | " + if(itemsViewModel?.LTP.isNullOrBlank()){"-"}else{java.lang.String.format(resources.getString(R.string.text_ltp), itemsViewModel?.LTP)}

        holder.symbolShortName.text = exchangeMap.get(itemsViewModel.Exchange_Name.toString())
        holder.root.setOnClickListener {
            clickListener.onItemClick(itemsViewModel)
        }
    }


    private fun setTextColorBasedOnOrderType(orderType: Int, holder: ViewHolder, ) {
        var color = ContextCompat.getColor(context,(R.color.purple_700))
        when(orderType){
            2 -> color = ContextCompat.getColor(context,(R.color.red))
        }
        holder.date.setTextColor(color)
        holder.statusQuantity.setTextColor(color)
        holder.symbolName.setTextColor(color)
        holder.symbolExpiry.setTextColor(color)
        holder.symbolPrice.setTextColor(color)
        holder.symbolShortName.setTextColor(color)
        holder.symbolLtp.setTextColor(color)
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
        val date: TextView = itemView.findViewById(R.id.date)
        val statusQuantity: TextView = itemView.findViewById(R.id.status_quantity)
        val symbolName: TextView = itemView.findViewById(R.id.symbolName)
        val symbolExpiry: TextView = itemView.findViewById(R.id.symbolExpiry)
        val symbolPrice: TextView = itemView.findViewById(R.id.symbolPrice)
        val symbolShortName: TextView = itemView.findViewById(R.id.symbolShortName)
        val symbolLtp: TextView = itemView.findViewById(R.id.symbolLtp)
        val root: View = itemView.findViewById(R.id.root)
    }

    interface ClickListener {
        fun onItemClick(model: PendingOrderModel)
    }
}
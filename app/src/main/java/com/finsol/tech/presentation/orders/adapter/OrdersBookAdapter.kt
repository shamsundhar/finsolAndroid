package com.finsol.tech.presentation.orders.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.finsol.tech.FinsolApplication
import com.finsol.tech.R
import com.finsol.tech.data.model.OrderHistoryModel
import com.finsol.tech.data.model.RejectedCancelledOrdersResponse
import com.finsol.tech.data.model.toOrderHistoryModel
import com.finsol.tech.rabbitmq.RabbitMQ
import com.finsol.tech.util.Utilities
import java.util.*

class OrdersBookAdapter(private val context: Context, private val resources: Resources): RecyclerView.Adapter<OrdersBookAdapter.ViewHolder>(){
    lateinit var clickListener:ClickListener
    private var exchangeMap:HashMap<String, String> = HashMap()
    private lateinit var mList: List<RejectedCancelledOrdersResponse>
    fun updateList(list: List<RejectedCancelledOrdersResponse>) {
        mList = list
        mList = mList.sortedByDescending { it.ExchangeTransactTime }
        mList.forEach {
            RabbitMQ.subscribeForMarketData(it.SecurityID!!)
        }
        notifyDataSetChanged()
    }

    fun exchangeMap(exchangeMap:HashMap<String, String>) {
        this.exchangeMap = exchangeMap
    }
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_orders_book, parent, false)

        return ViewHolder(view)
    }

    fun setOnItemClickListener(listener:ClickListener){
        clickListener = listener
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemsViewModel = mList[position]
        holder.date.text = Utilities.convertOrderHistoryTimeWithDate(itemsViewModel.ExchangeTransactTime)

//        holder.symbolName.text = itemsViewModel.ExchangeOderID
//        itemsViewModel.ContractYear.let {
//            holder.symbolExpiry.text =  itemsViewModel.MaturityDay.toString()+"-"+ Utilities.getMonthName(
//                itemsViewModel.ContractYear?.substring(4)!!.toInt(),
//                Locale.US, true)+"-"+itemsViewModel.ContractYear?.substring(2,4)
//        }

        val contract = (context.applicationContext as FinsolApplication).getContractBySecurityID(itemsViewModel.SecurityID.toString())
        holder.symbolName.text = contract?.symbolName
        contract?.expiry.let {
            holder.symbolExpiry.text = contract?.maturityDay +"-"+ Utilities.getMonthName(
                contract?.expiry?.substring(4,6)!!.toInt(),
                Locale.US, true) + "-" + contract.expiry.substring(0, 4)
        }

        holder.symbolPrice.text = itemsViewModel.PriceSend.toString()
        holder.orderQuantity.text = "${itemsViewModel.OrderStatus} | Qty: ${itemsViewModel.OrderQty}"
        holder.symbolShortName.text = exchangeMap.get(itemsViewModel.ExchangeName.toString())

        val marketType = itemsViewModel.MarketType.let {
            when(it){
                1 -> "MARKET"
                2 -> "LIMIT"
                3 -> "STOP"
                4 -> "STOPLIMIT"
                5 -> "ICEBERG"
                else -> ""
            }
        }

        holder.symbolLtp.text = "$marketType | " + if(itemsViewModel?.LTP.isNullOrBlank()){"-"}else{java.lang.String.format(resources.getString(R.string.text_ltp), itemsViewModel.LTP)}

        setTextColorBasedOnOrderType(itemsViewModel.OrderType!!,holder)
        holder.root.setOnClickListener {
            clickListener.onItemClick(itemsViewModel.toOrderHistoryModel())
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }


    private fun setTextColorBasedOnOrderType(orderType: Int, holder: ViewHolder) {
        println("")
        var color = ContextCompat.getColor(context,(R.color.purple_700))
        when(orderType){
            2 -> color = ContextCompat.getColor(context,(R.color.red))
        }
        holder.date.setTextColor(color)
        holder.orderQuantity.setTextColor(color)
        holder.symbolName.setTextColor(color)
        holder.symbolExpiry.setTextColor(color)
        holder.symbolPrice.setTextColor(color)
        holder.symbolShortName.setTextColor(color)
        holder.symbolLtp.setTextColor(color)

    }
    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val date: TextView = itemView.findViewById(R.id.date)
        val orderQuantity: TextView = itemView.findViewById(R.id.orderQuantity)
        val symbolName: TextView = itemView.findViewById(R.id.symbolName)
        val symbolExpiry: TextView = itemView.findViewById(R.id.symbolExpiry)
        val symbolPrice: TextView = itemView.findViewById(R.id.symbolPrice)
        val symbolShortName: TextView = itemView.findViewById(R.id.symbolShortName)
        val symbolLtp: TextView = itemView.findViewById(R.id.symbolLtp)


        val root: View = itemView.findViewById(R.id.root)
    }

    interface ClickListener {
        fun onItemClick(model: OrderHistoryModel)
    }
}
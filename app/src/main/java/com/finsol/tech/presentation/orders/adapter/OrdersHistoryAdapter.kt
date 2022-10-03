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
import com.finsol.tech.data.model.OrderHistoryModel
import com.finsol.tech.util.Utilities
import java.util.*

class OrdersHistoryAdapter(private val context: Context, private val resources: Resources): RecyclerView.Adapter<OrdersHistoryAdapter.ViewHolder>(){
    lateinit var clickListener:ClickListener
    private var exchangeMap:HashMap<String, String> = HashMap()

    private lateinit var mList: List<OrderHistoryModel>
    fun updateList(list: List<OrderHistoryModel>) {
        mList = list
        mList = mList.sortedByDescending { it.ExchangeTransactTime }
        notifyDataSetChanged()
    }

    fun exchangeMap(exchangeMap:HashMap<String, String>) {
        this.exchangeMap = exchangeMap
    }
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_orders_history_new, parent, false)

        return ViewHolder(view)
    }

    fun setOnItemClickListener(listener:ClickListener){
        clickListener = listener
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        // sets the text to the textview from our itemHolder class
//        holder.symbolName.text = itemsViewModel.ExchangeTradingID
//        itemsViewModel.ContractYear.let {
//            holder.symbolExpiry.text =  itemsViewModel.maturityDay.toString()+" "+ Utilities.getMonthName(
//                itemsViewModel.ContractYear.substring(4).toInt(),
//                Locale.US, true)
//        }
//        itemsViewModel.ContractYear.let {
//            holder.symbolExpiry.text =  itemsViewModel.maturityDay.toString()+"-"+ Utilities.getMonthName(
//                itemsViewModel.ContractYear.substring(4).toInt(),
//                Locale.US, true)+"-"+itemsViewModel.ContractYear.substring(2,4)
//        }

        val contract = (context.applicationContext as FinsolApplication).getContractBySecurityID(itemsViewModel.SecurityID)
        holder.symbolName.text = contract?.symbolName
        contract?.expiry.let {
            holder.symbolExpiry.text = contract?.maturityDay +"-"+ Utilities.getMonthName(
                contract?.expiry?.substring(4,6)!!.toInt(),
                Locale.US, true) + "-" + contract.expiry.substring(0, 4)
        }

        holder.symbolPrice.text = itemsViewModel.Price.toString()
        val ltp = itemsViewModel.LTP.toDouble()
        var pnl:Double = itemsViewModel.Order_Type.let {
            when (it) {
                1 -> (itemsViewModel.Price - ltp).toDouble()
                2 -> (ltp - itemsViewModel.Price).toDouble()
                else -> 0.0
            }
        }
//        if(pnl >= 0.0){
//            context?.let {holder.symbolPnl.setTextColor(ContextCompat.getColor(it,(R.color.green)))}
//        } else {
//            context?.let {holder.symbolPnl.setTextColor(ContextCompat.getColor(it,(R.color.red)))}
//        }
        pnl = Math.abs(pnl)
//        holder.symbolPnl.text = java.lang.String.format(resources.getString(R.string.text_pnl_percentage), pnl.toString()+"%")
        holder.statusQuantity.text = "Exec Qty: ${itemsViewModel.OrderQty}"


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

        holder.symbolLtp.text = "$marketType | "+ if(itemsViewModel?.LTP.isNullOrBlank()){"-"}else{java.lang.String.format(resources.getString(R.string.text_ltp), itemsViewModel.LTP)}
//        holder.orderQuantity.text = java.lang.String.format(resources.getString(R.string.text_work_quantity), itemsViewModel.OrderQty)
        holder.symbolShortName.text = exchangeMap.get(itemsViewModel.Exchange_Name.toString())
//        holder.status1.text = itemsViewModel.Order_Type.let {
//            when(it){
//                1 -> "Buy"
//                2 -> "Sell"
//                else -> ""
//            }
//        }

        holder.date.text = Utilities.convertOrderHistoryTimeWithDate(itemsViewModel.ExchangeTransactTime)

//        context?.let {
//            itemsViewModel.Order_Type.let {
//                when(it){
//                    1 -> {
//                        holder.status1.setBackgroundResource(R.drawable.bg_aliceblue_round_corners)
//                    }
//                    2 -> {
//                        holder.status1.setBackgroundResource(R.drawable.bg_light_red_round_corner)
//                    }
//                    else -> ""
//                }
//            }
//        }
//        val padding20dp = 20
//        val padding8dp = 8
//        val density = resources.displayMetrics.density
//        val paddingSidePixel = (padding20dp * density)
//        val paddingTopPixel = (padding8dp * density)
//        holder.status1.setPadding(paddingSidePixel.toInt(), paddingTopPixel.toInt(),paddingSidePixel.toInt(),paddingTopPixel.toInt())
        setTextColorBasedOnOrderType(itemsViewModel.Order_Type,holder)
        holder.root.setOnClickListener {
            clickListener.onItemClick(itemsViewModel)
        }

    }

    private fun setTextColorBasedOnOrderType(orderType: Int, holder: ViewHolder) {
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
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        //TODO use binding below
        val symbolName: TextView = itemView.findViewById(R.id.symbolName)
        val symbolExpiry: TextView = itemView.findViewById(R.id.symbolExpiry)
        val symbolPrice: TextView = itemView.findViewById(R.id.symbolPrice)
        val date: TextView = itemView.findViewById(R.id.date)
        val symbolLtp: TextView = itemView.findViewById(R.id.symbolLtp)
        val statusQuantity:TextView = itemView.findViewById(R.id.status_quantity)
        val symbolShortName: TextView = itemView.findViewById(R.id.symbolShortName)
        val root: View = itemView.findViewById(R.id.root)
    }

    interface ClickListener {
        fun onItemClick(model: OrderHistoryModel)
    }
}
package com.finsol.tech.presentation.portfolio.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.finsol.tech.R
import com.finsol.tech.data.model.PortfolioData
import com.finsol.tech.util.Utilities
import java.util.*
import kotlin.math.abs

class PortfolioAdapter(context: Context?) : RecyclerView.Adapter<PortfolioAdapter.ViewHolder>(){
    val context: Context? = context
    lateinit var clickListener:ClickListener
    private lateinit var mList: List<PortfolioData>
    private var exchangeMap:HashMap<String, String> = HashMap()
    fun updateList(list: List<PortfolioData>) {
        mList = list
        notifyDataSetChanged()
    }
    fun exchangeMap(exchangeMap:HashMap<String, String>) {
        this.exchangeMap = exchangeMap
    }
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_portfolio_new, parent, false)

        return ViewHolder(view)
    }

    fun setOnItemClickListener(listener:ClickListener){
        clickListener = listener
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        // sets the text to the textview from our itemHolder class
        holder.symbolNetPosition.text = "Net Pos: "+ abs(itemsViewModel.netPosition).toString()
        val avg:Double = if(itemsViewModel.netPosition > 0){
            itemsViewModel.avgBuyPrice
        } else {
            itemsViewModel.avgSellPrice
        }
        holder.symbolAvgPrice.text = "AVG: "+java.lang.String.format(context?.resources?.getString(R.string.text_cumulative_pnl), avg)
        holder.symbolName.text = itemsViewModel.productSymbol
//        itemsViewModel.contractYear.let {
//            holder.symbolExpiry.text = itemsViewModel.maturityDay +"-"+ Utilities.getMonthName(
//                itemsViewModel.contractYear.toString().substring(4).toInt(),
//                Locale.US, true)
//        }

        itemsViewModel.contractYear.let {
            holder.symbolExpiry.text = itemsViewModel.maturityDay +"-"+ Utilities.getMonthName(
                itemsViewModel.contractYear.toString().substring(4,6).toInt(),
                Locale.US, true) + "-" + itemsViewModel.contractYear.toString().substring(0, 4)
        }
        holder.symbolPrice.text = java.lang.String.format(context?.resources?.getString(R.string.text_cumulative_pnl), itemsViewModel.cumulativePNL)
//        val invested = abs(itemsViewModel.netPosition) *avg
        holder.exchangeName.text = exchangeMap.get(itemsViewModel.exchangeName.toString())
//        var status1Value:Double = ((itemsViewModel.cumulativePNL/invested)*100)
//        if(status1Value.isInfinite() || status1Value.isNaN()){
//            status1Value = 0.0
//        }
//        if(status1Value >= 0){
//            context?.let {holder.status1.setTextColor( ContextCompat.getColor(it,(R.color.green)) )}
//        } else {
//            context?.let {holder.status1.setTextColor( ContextCompat.getColor(it,(R.color.red)) )}
//        }
//        holder.status1.text = java.lang.String.format(context?.resources?.getString(R.string.text_cumulative_pnl), status1Value)+"%"

//        val ltpChangePercent = if(itemsViewModel?.LTPChangePercent.isNullOrBlank()){"(n/a)"}else{itemsViewModel?.LTPChangePercent}
//        //ltp change percent
//        if(ltpChangePercent != "(-)"){
//            if(ltpChangePercent.toDouble() >= 0){
//                context?.let {holder.symbolValue.setTextColor( ContextCompat.getColor(it,(R.color.green)) )}
//            } else {
//                context?.let {holder.symbolValue.setTextColor( ContextCompat.getColor(it,(R.color.red)) )}
//            }
//            holder.symbolValue.text = ltpChangePercent.toString()+"%"
//        } else {
//            context?.let {holder.symbolValue.setTextColor( ContextCompat.getColor(it,(R.color.red)) )}
//            holder.symbolValue.text = ltpChangePercent.toString()
//        }

            //ltp
        holder.ltp.text = if(itemsViewModel?.LTP.isNullOrBlank()){"LTP:n/a"}else{java.lang.String.format(context?.resources?.getString(R.string.text_ltp), itemsViewModel.LTP)}

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
        val symbolNetPosition:TextView = itemView.findViewById(R.id.netPosition)
        val symbolAvgPrice:TextView = itemView.findViewById(R.id.averagePrice)
        val symbolPrice: TextView = itemView.findViewById(R.id.symbolPrice)
        val exchangeName:TextView = itemView.findViewById(R.id.exchangeName)
        val ltp:TextView = itemView.findViewById(R.id.ltp)
        val root: View = itemView.findViewById(R.id.root)
    }

    interface ClickListener {
        fun onItemClick(model: PortfolioData)
    }
}
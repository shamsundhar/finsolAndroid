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

class PortfolioAdapter(context: Context?) : RecyclerView.Adapter<PortfolioAdapter.ViewHolder>(){
    val context: Context? = context
    lateinit var clickListener:ClickListener
    private lateinit var mList: List<PortfolioData>
    fun updateList(list: List<PortfolioData>) {
        mList = list
        notifyDataSetChanged()
    }
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_portfolio, parent, false)

        return ViewHolder(view)
    }

    fun setOnItemClickListener(listener:ClickListener){
        clickListener = listener
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        // sets the text to the textview from our itemHolder class
        holder.symbolQuantity.text = "Qty - "+itemsViewModel.netPosition.toString()
        val avg:Double
        if(itemsViewModel.netPosition > 0){
            avg = itemsViewModel.avgBuyPrice
        } else {
            avg = itemsViewModel.avgSellPrice
        }
        holder.symbolAvgPrice.text = "AVG - "+java.lang.String.format(context?.resources?.getString(R.string.text_cumulative_pnl), avg)
        holder.symbolName.text = itemsViewModel.productSymbol
        holder.symbolPrice.text = java.lang.String.format(context?.resources?.getString(R.string.text_cumulative_pnl), itemsViewModel.cumulativePNL)
        val invested = itemsViewModel.netPosition*avg
        holder.symbolInvested.text = "Invested - "+java.lang.String.format(context?.resources?.getString(R.string.text_cumulative_pnl), invested)
        val status1Value = ((itemsViewModel.cumulativePNL/invested)*100)
        if(status1Value >= 0){
            context?.let {holder.status1.setTextColor( ContextCompat.getColor(it,(R.color.green)) )}
        } else {
            context?.let {holder.status1.setTextColor( ContextCompat.getColor(it,(R.color.red)) )}
        }
        holder.status1.text = java.lang.String.format(context?.resources?.getString(R.string.text_cumulative_pnl), status1Value)+"%"
        val ltpChangePercent = if(itemsViewModel?.LTPChangePercent.isNullOrBlank()){"(-)"}else{itemsViewModel?.LTPChangePercent}
        //ltp change percent
        if(!ltpChangePercent.equals("(-)")){
            if(ltpChangePercent.toDouble() >= 0){
                context?.let {holder.symbolValue.setTextColor( ContextCompat.getColor(it,(R.color.green)) )}
            } else {
                context?.let {holder.symbolValue.setTextColor( ContextCompat.getColor(it,(R.color.red)) )}
            }
        } else {
            context?.let {holder.symbolValue.setTextColor( ContextCompat.getColor(it,(R.color.red)) )}
        }
        holder.symbolValue.text = ltpChangePercent.toString()
            //ltp
        holder.symbolValue2.text = "LTP - "+if(itemsViewModel?.LTP.isNullOrBlank()){"-"}else{itemsViewModel?.LTP.toString()}

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
        val symbolQuantity:TextView = itemView.findViewById(R.id.symbolQuantity)
        val symbolAvgPrice:TextView = itemView.findViewById(R.id.averagePrice)
        val symbolPrice: TextView = itemView.findViewById(R.id.symbolPrice)
        val symbolInvested:TextView = itemView.findViewById(R.id.symbolInvested)
        val symbolValue2:TextView = itemView.findViewById(R.id.symbolValue2)
        val symbolValue:TextView = itemView.findViewById(R.id.symbolValue)
        val status1:TextView = itemView.findViewById(R.id.status1)
        val root: View = itemView.findViewById(R.id.root)
    }

    interface ClickListener {
        fun onItemClick(model: PortfolioData)
    }
}
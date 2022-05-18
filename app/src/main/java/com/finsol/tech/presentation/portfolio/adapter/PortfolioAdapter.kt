package com.finsol.tech.presentation.portfolio.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.finsol.tech.R
import com.finsol.tech.data.model.OrderHistoryModel
import com.finsol.tech.data.model.PortfolioData
import com.finsol.tech.data.model.PortfolioResponse
import com.finsol.tech.presentation.watchlist.WatchListModel

class PortfolioAdapter: RecyclerView.Adapter<PortfolioAdapter.ViewHolder>(){
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
        if(itemsViewModel.netPosition > 0){
            holder.symbolAvgPrice.text = "AVG - "+itemsViewModel.avgBuyPrice.toString()
        } else {
            holder.symbolAvgPrice.text = "AVG - "+itemsViewModel.avgSellPrice.toString()
        }
        holder.symbolName.text = itemsViewModel.productSymbol
        holder.symbolPrice.text = itemsViewModel.cumulativePNL.toString()
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
        val root: View = itemView.findViewById(R.id.root)
    }

    interface ClickListener {
        fun onItemClick(model: PortfolioData)
    }
}
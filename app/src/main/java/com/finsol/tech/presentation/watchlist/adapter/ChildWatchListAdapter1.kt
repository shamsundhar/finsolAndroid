package com.finsol.tech.presentation.watchlist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.finsol.tech.R
import com.finsol.tech.data.model.Contracts
import com.finsol.tech.presentation.watchlist.WatchListDiffUtils

class ChildWatchListAdapter1: RecyclerView.Adapter<ChildWatchListAdapter1.ViewHolder>(){
    lateinit var clickListener:ClickListener
    private lateinit var mList: List<Contracts>

    fun updateList(list: List<Contracts>) {
//        mList = list
//        notifyDataSetChanged()

        var oldList = if (::mList.isInitialized) mList else listOf()
        val diffUtil = WatchListDiffUtils(oldList,list)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        mList = list
        diffResult.dispatchUpdatesTo(this)


    }
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_watchlist, parent, false)

        return ViewHolder(view)
    }

    fun setOnItemClickListener(listener:ClickListener){
        clickListener = listener
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]
        val change = itemsViewModel.lTP - itemsViewModel.closePrice
        val changePercent:Float
        if(itemsViewModel.closePrice != 0f){
             changePercent = ((change/itemsViewModel.closePrice)*100).toFloat()
        }
        else {
             changePercent = ((change)*100).toFloat()
        }
        // sets the text to the textview from our itemHolder class
        holder.symbolName.text = itemsViewModel.displayName
        holder.symbolPrice.text = itemsViewModel.lTP.toString()
        holder.symbolTime.text = itemsViewModel.updatedTime
        holder.symbolCity.text = itemsViewModel.exchangeName
        holder.symbolValue.text = changePercent.toString()+"%"
        holder.root.setOnClickListener {
            clickListener.onItemClick(itemsViewModel)
        }
        holder.root.setOnLongClickListener{
                clickListener.onItemLongClick()
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
        val symbolTime: TextView = itemView.findViewById(R.id.symbolTime)
        val symbolCity: TextView = itemView.findViewById(R.id.symbolCity)
        val symbolValue: TextView = itemView.findViewById(R.id.symbolValue)
        val root: View = itemView.findViewById(R.id.root)
    }

    interface ClickListener {
        fun onItemClick(model: Contracts)
        fun onItemLongClick():Boolean
    }
}
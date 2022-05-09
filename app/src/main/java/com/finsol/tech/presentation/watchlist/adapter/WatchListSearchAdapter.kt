package com.finsol.tech.presentation.watchlist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.finsol.tech.R
import com.finsol.tech.data.model.Contracts
import com.finsol.tech.presentation.watchlist.WatchListModel

class WatchListSearchAdapter: RecyclerView.Adapter<WatchListSearchAdapter.ViewHolder>(){
    lateinit var clickListener:ClickListener
    private lateinit var mList: List<Contracts>

    fun updateList(list: List<Contracts>) {
        mList = list
        notifyDataSetChanged()
    }
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_watchlist_search, parent, false)

        return ViewHolder(view)
    }

    fun setOnItemClickListener(listener:ClickListener){
        clickListener = listener
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        // sets the text to the textview from our itemHolder class
        holder.symbolName.text = itemsViewModel.displayName
        holder.symbolTime.text = itemsViewModel.updatedTime
        holder.symbolCity.text = itemsViewModel.exchangeName
        if(!itemsViewModel.isAddedToWatchList)
        holder.imageView.setImageResource(R.drawable.ic_plus)
        else holder.imageView.setImageResource(R.drawable.ic_tick_grey)
        holder.imageView.setOnClickListener {
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
        val symbolTime: TextView = itemView.findViewById(R.id.symbolTime)
        val symbolCity: TextView = itemView.findViewById(R.id.symbolCity)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val root: View = itemView.findViewById(R.id.root)
    }

    interface ClickListener {
        fun onItemClick(model: Contracts)
    }
}
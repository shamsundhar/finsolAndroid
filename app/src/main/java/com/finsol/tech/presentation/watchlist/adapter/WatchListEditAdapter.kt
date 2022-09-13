package com.finsol.tech.presentation.watchlist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.finsol.tech.R
import com.finsol.tech.data.model.Contracts
import com.finsol.tech.util.Utilities
import java.util.*

class WatchListEditAdapter: RecyclerView.Adapter<WatchListEditAdapter.ViewHolder>(){
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
            .inflate(R.layout.item_watchlist_edit, parent, false)

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
        holder.symbolCity.text = itemsViewModel.exchangeName
        itemsViewModel.expiry.let {
            holder.symbolExpiry.text = itemsViewModel.maturityDay +"-"+ Utilities.getMonthName(
                itemsViewModel.expiry.substring(4).toInt(),
                Locale.US, true) + "-" + itemsViewModel.expiry.substring(0, 4)
        }
        holder.delete.setOnClickListener {
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
        val symbolCity: TextView = itemView.findViewById(R.id.symbolCity)
        val delete: ImageView = itemView.findViewById(R.id.delete)
        val root: View = itemView.findViewById(R.id.root)
    }

    interface ClickListener {
        fun onItemClick(model: Contracts)
    }
}